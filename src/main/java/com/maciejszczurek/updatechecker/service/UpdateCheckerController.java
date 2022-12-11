package com.maciejszczurek.updatechecker.service;

import static javafx.scene.control.Alert.AlertType;

import com.github.mouse0w0.darculafx.DarculaFX;
import com.maciejszczurek.updatechecker.UpdateCheckerFxApplication;
import com.maciejszczurek.updatechecker.application.model.Application;
import com.maciejszczurek.updatechecker.application.repository.ApplicationRepository;
import com.maciejszczurek.updatechecker.application.service.ApplicationList;
import com.maciejszczurek.updatechecker.application.utils.ApplicationUtils;
import com.maciejszczurek.updatechecker.checker.UpdateChecker;
import com.maciejszczurek.updatechecker.javafx.AlertBundle;
import com.maciejszczurek.updatechecker.stage.StageException;
import com.maciejszczurek.updatechecker.util.UrlUtils;
import com.pivovarit.function.exception.WrappedException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@SuppressWarnings("unused")
public class UpdateCheckerController implements Initializable {

  public static final double WIDTH = 735.;
  public static final double HEIGHT = 457.;
  public static final String EXCEPTION_FOR_MESSAGE = "Exception for %s. %s: %s";
  private final ApplicationContext context;
  private final ApplicationRepository repository;
  private final ThreadPoolTaskExecutor taskExecutor;
  private final UpdateCheckerFxApplication fxApplication;
  private final ObservableList<CheckResult> applicationsToUpdateList = FXCollections.observableArrayList();
  private final UpdateCheckerFactory updateCheckerFactory;
  private Task<Void> task;

  @FXML
  private ProgressBar progressBar;

  @FXML
  private Button checkButton;

  @FXML
  private Button checkIgnoredButton;

  @FXML
  private TableView<CheckResult> table;

  @FXML
  private Button updateButton;

  private Stage stage;

  private ResourceBundle resourceBundle;

  public UpdateCheckerController(
    final ApplicationContext context,
    final ApplicationRepository repository,
    final ThreadPoolTaskExecutor taskExecutor,
    @SuppressWarnings(
      "SpringJavaInjectionPointsAutowiringInspection"
    ) final UpdateCheckerFxApplication fxApplication,
    final UpdateCheckerFactory updateCheckerFactory
  ) {
    this.context = context;
    this.repository = repository;
    this.taskExecutor = taskExecutor;
    this.fxApplication = fxApplication;
    this.updateCheckerFactory = updateCheckerFactory;

    UserAgents.generateUserAgent();
  }

  private void check(final boolean withIgnored) {
    applicationsToUpdateList.clear();

    progressBar.setVisible(true);
    checkIgnoredButton.setDisable(true);
    checkButton.setText(resourceBundle.getString("cancel"));

    final var errors = ConcurrentHashMap.<String>newKeySet();
    final var items = FXCollections.synchronizedObservableList(
      applicationsToUpdateList
    );

    final var sortedItems = items.sorted();
    sortedItems.comparatorProperty().bind(table.comparatorProperty());
    table.setItems(sortedItems);

    task =
      new Task<>() {
        @Override
        protected Void call() {
          final var applications = withIgnored
            ? repository.findAll()
            : repository.findByIgnoredIsFalse();
          final var currentCount = new LongAdder();
          updateProgress(0L, applications.size());

          CompletableFuture
            .allOf(
              applications
                .stream()
                .map(application ->
                  CompletableFuture.runAsync(
                    getRunnable(
                      () -> {
                        currentCount.increment();
                        updateProgress(
                          currentCount.longValue(),
                          applications.size()
                        );
                      },
                      items,
                      application,
                      updateCheckerFactory.getUpdateChecker(application),
                      errors
                    ),
                    taskExecutor
                  )
                )
                .toArray(CompletableFuture[]::new)
            )
            .join();

          return null;
        }
      };

    task.setOnSucceeded(getOnTaskEndEventHandler(errors));
    task.setOnCancelled(getOnTaskEndEventHandler(errors));
    task.setOnFailed(getOnTaskEndEventHandler(errors));
    progressBar.progressProperty().bind(task.progressProperty());

    taskExecutor.execute(task);
  }

  @NotNull
  private EventHandler<WorkerStateEvent> getOnTaskEndEventHandler(
    final Set<String> errors
  ) {
    return event -> {
      if (!errors.isEmpty()) {
        final var alert = new Alert(AlertType.ERROR, String.join("\n", errors));
        alert.setTitle(AlertBundle.ERROR);
        alert.setHeaderText(AlertBundle.ERROR);
        alert.showAndWait();
      }

      progressBar.setVisible(false);
      checkIgnoredButton.setDisable(false);
      checkButton.setText(resourceBundle.getString("check"));
    };
  }

  @Contract(pure = true)
  @NotNull
  private Runnable getRunnable(
    final Runnable afterChecked,
    final ObservableList<CheckResult> table,
    final Application application,
    final UpdateChecker updateChecker,
    final Set<String> errors
  ) {
    return () -> {
      for (var i = 1; i <= 2; i++) {
        if (task.isCancelled()) {
          break;
        }

        try {
          updateChecker.checkUpdate();

          if (updateChecker.isUpdate()) {
            table.add(
              new CheckResult(
                ApplicationUtils.getUpdateUrl(application),
                updateChecker,
                application
              )
            );
          }

          break;
        } catch (WrappedException e) {
          errors.add(
            EXCEPTION_FOR_MESSAGE.formatted(
              application.getName(),
              e.getCause().getClass().getSimpleName(),
              e.getLocalizedMessage()
            )
          );
        } catch (
          IOException
          | UncheckedIOException
          | NullPointerException
          | StringIndexOutOfBoundsException e
        ) {
          errors.add(
            EXCEPTION_FOR_MESSAGE.formatted(
              application.getName(),
              e.getClass().getSimpleName(),
              e.getLocalizedMessage()
            )
          );
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          errors.add(
            EXCEPTION_FOR_MESSAGE.formatted(
              application.getName(),
              e.getClass().getSimpleName(),
              e.getLocalizedMessage()
            )
          );
        }
      }

      afterChecked.run();
    };
  }

  @FXML
  private void closeAction(final ActionEvent event) {
    fxApplication.stop();
  }

  @Override
  public void initialize(
    final URL location,
    @NotNull final ResourceBundle resources
  ) {
    table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    table
      .getSelectionModel()
      .selectedItemProperty()
      .addListener((observable, oldValue, newValue) ->
        updateButton.setDisable(newValue == null)
      );

    table.getColumns().clear();

    var nameColumn = new TableColumn<CheckResult, String>(
      resources.getString("application-name")
    );
    nameColumn.setPrefWidth(291.);
    nameColumn.setCellValueFactory(param ->
      new SimpleStringProperty(param.getValue().getApplication().getName())
    );
    table.getColumns().add(nameColumn);

    var newVersionColumn = new TableColumn<CheckResult, String>(
      resources.getString("new-version")
    );
    newVersionColumn.setPrefWidth(125.);
    newVersionColumn.setSortable(false);
    newVersionColumn.setCellValueFactory(param ->
      new SimpleStringProperty(
        param.getValue().getUpdateChecker().getNewVersion()
      )
    );
    table.getColumns().add(newVersionColumn);

    var currentVersionColumn = new TableColumn<CheckResult, String>(
      resources.getString("current-version")
    );
    currentVersionColumn.setPrefWidth(106.);
    currentVersionColumn.setSortable(false);
    currentVersionColumn.setCellValueFactory(param ->
      new SimpleStringProperty(
        param.getValue().getUpdateChecker().getCurrentVersion()
      )
    );
    table.getColumns().add(currentVersionColumn);

    var updateUrlColumn = new TableColumn<CheckResult, String>(
      resources.getString("update-url")
    );
    updateUrlColumn.setPrefWidth(211.);
    updateUrlColumn.setSortable(false);
    updateUrlColumn.setCellValueFactory(
      new PropertyValueFactory<>("updateUrl")
    );
    table.getColumns().add(updateUrlColumn);

    table.getSortOrder().add(nameColumn);

    table.setRowFactory(this::rowFactory);
  }

  @NotNull
  private TableRow<CheckResult> rowFactory(
    final TableView<CheckResult> tableView
  ) {
    final var tableRow = new TableRow<CheckResult>();

    tableRow.setOnMouseClicked(event -> {
      if (event.getClickCount() == 2) {
        try {
          UrlUtils.openUrl(
            tableView.getSelectionModel().getSelectedItem().getUpdateUrl()
          );
        } catch (IOException e) {
          final var alert = new Alert(AlertType.ERROR, e.getLocalizedMessage());
          alert.setTitle(AlertBundle.ERROR);
          alert.setHeaderText(AlertBundle.ERROR);
          alert.showAndWait();
        }
      }
    });

    return tableRow;
  }

  @FXML
  public void updateSelectedButtonAction(final ActionEvent event) {
    final var applicationsToUpdate = new ArrayList<Application>();

    List
      .copyOf(table.getSelectionModel().getSelectedItems())
      .forEach(selectedApplication -> {
        final var application = selectedApplication.getApplication();
        final var updateChecker = selectedApplication.getUpdateChecker();

        updateChecker.update();

        application.setCurrentVersion(updateChecker.getCurrentVersion());

        applicationsToUpdate.add(
          application.setIgnored(false).setLastUpdate(LocalDateTime.now())
        );

        applicationsToUpdateList.remove(selectedApplication);
      });

    repository.saveAll(applicationsToUpdate);
  }

  @FXML
  public void checkButtonAction(final ActionEvent event) {
    if (task == null || !task.isRunning()) {
      check(false);
    } else {
      task.cancel();
    }
  }

  @FXML
  public void checkIgnoredButtonAction(final ActionEvent event) {
    if (task == null || !task.isRunning()) {
      check(true);
    } else {
      task.cancel();
    }
  }

  @FXML
  public void editAction(final ActionEvent event) throws IOException {
    if (task != null) {
      task.cancel();
    }
    context.getBean(ApplicationList.class).open(stage);
    table.setItems(FXCollections.emptyObservableList());
  }

  @EventListener
  public void stageReadyEvent(
    @NotNull final UpdateCheckerFxApplication.PrimaryStageReadyEvent event
  ) {
    try {
      stage = event.getStage();
      final var fxmlLoader = new FXMLLoader(
        context.getResource("classpath:main.fxml").getURL()
      );
      resourceBundle = ResourceBundle.getBundle("main");
      fxmlLoader.setResources(resourceBundle);
      fxmlLoader.setControllerFactory(context::getBean);
      stage.setScene(new Scene(fxmlLoader.load(), WIDTH, HEIGHT));
      stage.setMinWidth(WIDTH + 16);
      stage.setMinHeight(HEIGHT + 39);
      stage.setTitle(resourceBundle.getString("update-checker"));
      DarculaFX.applyDarculaStyle(stage.getScene());
      //      new JMetro(Style.DARK).setScene(stage.getScene());
      stage.show();
    } catch (IOException e) {
      throw new StageException(e);
    }
  }

  @SuppressWarnings("unused")
  public static final class CheckResult {

    private final SimpleStringProperty updateUrl;

    @Getter
    private final UpdateChecker updateChecker;

    @Getter
    private final Application application;

    private CheckResult(
      final String updateUrl,
      final UpdateChecker updateChecker,
      final Application application
    ) {
      this.updateUrl = new SimpleStringProperty(updateUrl);
      this.updateChecker = updateChecker;
      this.application = application;
    }

    public String getUpdateUrl() {
      return updateUrl.get();
    }

    public void setUpdateUrl(String updateUrl) {
      this.updateUrl.set(updateUrl);
    }

    public SimpleStringProperty updateUrlProperty() {
      return updateUrl;
    }
  }
}
