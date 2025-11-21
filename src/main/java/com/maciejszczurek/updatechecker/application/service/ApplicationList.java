package com.maciejszczurek.updatechecker.application.service;

import static java.time.temporal.ChronoField.HOUR_OF_DAY;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;

import com.github.mouse0w0.darculafx.DarculaFX;
import com.maciejszczurek.updatechecker.application.model.Application;
import com.maciejszczurek.updatechecker.application.model.ApplicationType;
import com.maciejszczurek.updatechecker.application.repository.ApplicationRepository;
import com.maciejszczurek.updatechecker.javafx.AlertBundle;
import com.maciejszczurek.updatechecker.javafx.ButtonTypes;
import com.maciejszczurek.updatechecker.service.UpdateCheckerFactory;
import com.maciejszczurek.updatechecker.util.UrlUtils;
import jakarta.persistence.EntityManager;
import jakarta.validation.Validator;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.TextStyle;
import java.time.temporal.ChronoField;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.converter.DefaultStringConverter;
import javafx.util.converter.LocalDateTimeStringConverter;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.InvalidJobParametersException;
import org.springframework.batch.core.launch.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.launch.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.launch.JobRestartException;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

@Service
public class ApplicationList implements Initializable {

  public static final double WIDTH = 1358.0;
  public static final double HEIGHT = 719.;
  private static final DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder()
    .parseCaseInsensitive()
    .appendValue(ChronoField.DAY_OF_MONTH)
    .appendLiteral(' ')
    .appendText(ChronoField.MONTH_OF_YEAR, TextStyle.SHORT)
    .appendLiteral(' ')
    .appendValue(ChronoField.YEAR, 4)
    .appendLiteral(' ')
    .appendValue(HOUR_OF_DAY, 2)
    .appendLiteral(':')
    .appendValue(MINUTE_OF_HOUR, 2)
    .toFormatter();

  private final ApplicationRepository repository;
  private final Validator validator;
  private final UpdateCheckerFactory updateCheckerFactory;
  private final ThreadPoolTaskExecutor taskExecutor;
  private final ObservableList<ApplicationRow> applicationItems = FXCollections.observableArrayList();
  private final ImporterExporter importerExporter;
  private final JpaEntityInformation<Application, ?> persistentEntity;
  private final ApplicationContext context;
  private boolean isDuringItemsReload = false;

  @FXML
  private Button deleteButton;

  @FXML
  private TableView<ApplicationRow> table;

  @FXML
  private ChoiceBox<String> searchTypeChoiceBox;

  @FXML
  private TextField searchTextField;

  private Stage stage;

  private ResourceBundle resourceBundle;

  public ApplicationList(
    final ApplicationRepository repository,
    final Validator validator,
    final UpdateCheckerFactory updateCheckerFactory,
    final ThreadPoolTaskExecutor taskExecutor,
    final ImporterExporter importerExporter,
    @NotNull final ApplicationContext context
  ) {
    this.repository = repository;
    this.validator = validator;
    this.updateCheckerFactory = updateCheckerFactory;
    this.taskExecutor = taskExecutor;
    this.importerExporter = importerExporter;
    this.context = context;
    persistentEntity =
      JpaEntityInformationSupport.getEntityInformation(
        Application.class,
        context.getBean(EntityManager.class)
      );
  }

  @FXML
  public void unusedApplicationButtonAction(ActionEvent event) {
    final Set<ApplicationType> applicationTypes = EnumSet.allOf(
      ApplicationType.class
    );

    repository
      .findAll()
      .stream()
      .map(Application::getApplicationType)
      .forEach(applicationTypes::remove);

    final var alert = new Alert(
      AlertType.NONE,
      applicationTypes
        .stream()
        .map(Enum::name)
        .collect(Collectors.joining("\n")),
      ButtonType.OK
    );
    alert.setTitle(resourceBundle.getString("unused-application-types"));
    alert.setHeaderText(resourceBundle.getString("unused-application-types"));
    alert.show();
  }

  @Override
  public void initialize(
    final URL location,
    final @NotNull ResourceBundle resources
  ) {
    reloadApplicationItems();
    applicationItems.addListener(this::applicationItemsChange);

    searchTypeChoiceBox.setItems(
      FXCollections.observableList(
        Stream
          .concat(
            Stream.of(""),
            EnumSet
              .allOf(ApplicationType.class)
              .stream()
              .map(ApplicationType::name)
          )
          .toList()
      )
    );
    searchTextField.setOnAction(this::search);
    searchTypeChoiceBox.setOnAction(this::search);

    table
      .getSelectionModel()
      .selectedItemProperty()
      .addListener((observable, oldValue, newValue) ->
        deleteButton.setDisable(newValue == null)
      );

    table.getColumns().clear();
    var sortedItems = applicationItems.sorted();
    sortedItems.comparatorProperty().bind(table.comparatorProperty());
    table.setItems(sortedItems);

    var nameColumn = new TableColumn<ApplicationRow, String>(
      resources.getString("application-name")
    );
    nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
    nameColumn.setPrefWidth(291.);
    nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
    nameColumn.setOnEditCommit(this::nameEditCommit);
    table.getColumns().add(nameColumn);

    var typeColumn = new TableColumn<ApplicationRow, ApplicationType>(
      resources.getString("type")
    );
    typeColumn.setCellFactory(
      ChoiceBoxTableCell.forTableColumn(
        Arrays
          .stream(ApplicationType.values())
          .collect(Collectors.toCollection(FXCollections::observableArrayList))
      )
    );
    typeColumn.setPrefWidth(132.);
    typeColumn.setCellValueFactory(
      new PropertyValueFactory<>("applicationType")
    );
    typeColumn.setOnEditCommit(event -> {
      var applicationRow = event
        .getTableView()
        .getItems()
        .get(event.getTablePosition().getRow());
      applicationRow.setApplicationType(event.getNewValue());
      update(applicationRow);
    });
    table.getColumns().add(typeColumn);

    var currentVersionColumn = new TableColumn<ApplicationRow, String>(
      resources.getString("current-version")
    );
    currentVersionColumn.setCellFactory(TextFieldTableCell.forTableColumn());
    currentVersionColumn.setPrefWidth(125.);
    currentVersionColumn.setCellValueFactory(
      new PropertyValueFactory<>("currentVersion")
    );
    currentVersionColumn.setOnEditCommit(event -> {
      var applicationRow = event
        .getTableView()
        .getItems()
        .get(event.getTablePosition().getRow());
      applicationRow.setCurrentVersion(event.getNewValue());
      update(applicationRow);
    });
    table.getColumns().add(currentVersionColumn);

    var siteUrlColumn = new TableColumn<ApplicationRow, String>(
      resources.getString("site-url")
    );
    siteUrlColumn.setCellFactory(this::openUrlCellFactory);
    siteUrlColumn.setPrefWidth(291.);
    siteUrlColumn.setCellValueFactory(new PropertyValueFactory<>("siteUrl"));
    siteUrlColumn.setOnEditCommit(event -> {
      var applicationRow = event
        .getTableView()
        .getItems()
        .get(event.getTablePosition().getRow());
      applicationRow.setSiteUrl(event.getNewValue());
      update(applicationRow);
    });
    table.getColumns().add(siteUrlColumn);

    var updateUrlColumn = new TableColumn<ApplicationRow, String>(
      resources.getString("update-url")
    );
    updateUrlColumn.setCellFactory(this::openUrlCellFactory);
    updateUrlColumn.setPrefWidth(291.);
    updateUrlColumn.setCellValueFactory(
      new PropertyValueFactory<>("updateUrl")
    );
    updateUrlColumn.setOnEditCommit(event -> {
      var applicationRow = event
        .getTableView()
        .getItems()
        .get(event.getTablePosition().getRow());
      applicationRow.setUpdateUrl(event.getNewValue());
      update(applicationRow);
    });
    table.getColumns().add(updateUrlColumn);

    var lastUpdateColumn = new TableColumn<ApplicationRow, LocalDateTime>(
      resources.getString("last-update")
    );
    lastUpdateColumn.setEditable(false);
    lastUpdateColumn.setPrefWidth(139.);
    lastUpdateColumn.setCellValueFactory(
      new PropertyValueFactory<>("lastUpdated")
    );
    lastUpdateColumn.setCellFactory(
      TextFieldTableCell.forTableColumn(
        new LocalDateTimeStringConverter(dateTimeFormatter, null)
      )
    );
    table.getColumns().add(lastUpdateColumn);

    var ignoredColumn = new TableColumn<ApplicationRow, Boolean>(
      resources.getString("ignored")
    );
    ignoredColumn.setEditable(true);
    ignoredColumn.setCellFactory(
      CheckBoxTableCell.forTableColumn(ignoredColumn)
    );
    ignoredColumn.setPrefWidth(74.);
    ignoredColumn.setResizable(false);
    ignoredColumn.setCellValueFactory(new PropertyValueFactory<>("ignored"));
    ignoredColumn.setOnEditCommit(event -> {
      var applicationRow = event
        .getTableView()
        .getItems()
        .get(event.getTablePosition().getRow());
      applicationRow.setIgnored(event.getNewValue());
      update(applicationRow);
    });
    table.getColumns().add(ignoredColumn);

    table.getSortOrder().add(nameColumn);
  }

  private void reloadApplicationItems() {
    isDuringItemsReload = true;

    applicationItems.clear();
    applicationItems.addAll(
      repository.findAll().stream().map(ApplicationRow::new).toList()
    );
    applicationItems.forEach(applicationRow ->
      applicationRow
        .ignoredProperty()
        .addListener(ignoredPropertyChange(applicationRow))
    );

    isDuringItemsReload = false;
  }

  @NotNull
  private TextFieldTableCell<ApplicationRow, String> openUrlCellFactory(
    final TableColumn<ApplicationRow, String> value
  ) {
    final var tableCell = new TextFieldTableCell<ApplicationRow, String>(
      new DefaultStringConverter()
    );

    final var menuItem = new MenuItem(resourceBundle.getString("open-url"));
    menuItem.setOnAction(event -> {
      final var item = tableCell.getItem();

      if (item != null && !item.isEmpty()) {
        try {
          UrlUtils.openUrl(item);
        } catch (IOException e) {
          final var alert = new Alert(AlertType.ERROR, e.getLocalizedMessage());
          alert.setTitle(AlertBundle.ERROR);
          alert.setHeaderText(AlertBundle.ERROR);
          alert.showAndWait();
        }
      }
    });
    tableCell.setContextMenu(new ContextMenu());
    tableCell.getContextMenu().getItems().add(menuItem);
    tableCell.setOnContextMenuRequested(event ->
      menuItem.setDisable(
        tableCell.getItem() == null || tableCell.getItem().isEmpty()
      )
    );

    return tableCell;
  }

  private void applicationItemsChange(
    final ListChangeListener.Change<? extends ApplicationList.ApplicationRow> items
  ) {
    if (isDuringItemsReload) {
      return;
    }

    while (items.next()) {
      if (items.wasRemoved()) {
        repository.deleteAllById(
          items
            .getRemoved()
            .stream()
            .map(ApplicationRow::getId)
            .filter(id -> id != null && id != 0L)
            .toList()
        );
      }
    }
  }

  private void nameEditCommit(
    @NotNull final TableColumn.CellEditEvent<ApplicationRow, String> event
  ) {
    final var applicationRow = event
      .getTableView()
      .getItems()
      .get(event.getTablePosition().getRow());
    final var newValue = event.getNewValue();

    if (repository.existsByNameAndIdNot(newValue, applicationRow.getId())) {
      event.getTableView().refresh();
      final var alert = new Alert(
        AlertType.ERROR,
        resourceBundle.getString("application-exists").formatted(newValue)
      );
      alert.setTitle(AlertBundle.ERROR);
      alert.setHeaderText(AlertBundle.ERROR);
      alert.show();
    } else {
      applicationRow.setName(event.getNewValue());
      update(applicationRow);
    }
  }

  @SneakyThrows
  private void update(@NotNull final ApplicationRow applicationRow) {
    var application = new Application()
      .setId(applicationRow.getId())
      .setName(applicationRow.getName())
      .setApplicationType(applicationRow.getApplicationType())
      .setCurrentVersion(applicationRow.getCurrentVersion())
      .setSiteUrl(applicationRow.getSiteUrl())
      .setUpdateUrl(applicationRow.getUpdateUrl())
      .setIgnored(applicationRow.isIgnored());

    if (
      application.getApplicationType() != null &&
      application.getSiteUrl() != null &&
      !application.getSiteUrl().isEmpty() &&
      (
        application.getCurrentVersion() == null ||
        application.getCurrentVersion().isEmpty()
      )
    ) {
      final var task = new Task<Void>() {
        @Override
        protected @Nullable Void call() throws Exception {
          final var updateChecker = updateCheckerFactory.getUpdateChecker(
            application
          );
          updateChecker.checkUpdate();
          updateChecker.update();

          applicationRow.setCurrentVersion(updateChecker.getNewVersion());
          update(applicationRow);

          return null;
        }
      };
      task.setOnFailed(event -> {
        final var exception = event.getSource().getException();
        final var alert = new Alert(
          AlertType.ERROR,
          "%s: %s".formatted(
              exception.getClass().getSimpleName(),
              exception.getLocalizedMessage()
            )
        );
        alert.setTitle(AlertBundle.ERROR);
        alert.setHeaderText(AlertBundle.ERROR);
        alert.show();
      });
      taskExecutor.execute(task);
    }

    if (validator.validate(application).isEmpty()) {
      final var isNew = persistentEntity.isNew(application);

      repository.save(application);

      if (isNew) {
        applicationRow.setId(application.getId());
      }
    }
  }

  private void search(final ActionEvent event) {
    final var type = Optional.ofNullable(searchTypeChoiceBox.getValue());
    final var name = searchTextField.getText();

    var items = type.isEmpty() || type.get().isEmpty()
      ? applicationItems
      : applicationItems
        .filtered(applicationRow ->
          Objects.nonNull(applicationRow.getApplicationType())
        )
        .filtered(applicationRow ->
          applicationRow
            .getApplicationType()
            .equals(ApplicationType.valueOf(type.get()))
        );
    items =
      name.isEmpty()
        ? items
        : items
          .filtered(applicationRow -> Objects.nonNull(applicationRow.getName()))
          .filtered(applicationRow ->
            applicationRow.getName().toLowerCase().contains(name.toLowerCase())
          );
    var sortedItems = items.sorted();
    sortedItems.comparatorProperty().bind(table.comparatorProperty());

    table.setItems(sortedItems);
  }

  @FXML
  public void addButtonAction(final ActionEvent event) {
    final var applicationRow = new ApplicationRow();
    applicationRow
      .ignoredProperty()
      .addListener(ignoredPropertyChange(applicationRow));

    Optional
      .ofNullable(searchTypeChoiceBox.getValue())
      .filter(type -> !type.isEmpty())
      .ifPresent(type ->
        applicationRow.setApplicationType(
          ApplicationType.valueOf(searchTypeChoiceBox.getValue())
        )
      );

    applicationItems.add(applicationRow);
    table.scrollTo(applicationRow);
  }

  @Contract(pure = true)
  private @NotNull ChangeListener<? super Boolean> ignoredPropertyChange(
    final ApplicationRow applicationRow
  ) {
    return (observable, oldValue, newValue) -> update(applicationRow);
  }

  @FXML
  public void deleteButtonAction(final ActionEvent event) {
    applicationItems.remove(table.getSelectionModel().getSelectedItem());
  }

  @FXML
  public void importButtonAction(final ActionEvent event)
    throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, InvalidJobParametersException, JobRestartException {
    if (repository.count() > 0) {
      var confirmDeleteAlert = new Alert(
        AlertType.CONFIRMATION,
        resourceBundle.getString("import-delete-text-confirmation"),
        ButtonTypes.YES,
        ButtonTypes.NO
      );
      confirmDeleteAlert.setTitle(resourceBundle.getString("confirm-deletion"));
      confirmDeleteAlert.setHeaderText(
        resourceBundle.getString("application-import")
      );

      if (
        confirmDeleteAlert
          .showAndWait()
          .map(buttonType -> buttonType.equals(ButtonTypes.NO))
          .orElse(false)
      ) {
        return;
      }
    }

    var fileChooser = new FileChooser();
    fileChooser.setTitle(resourceBundle.getString("select-where-import"));
    fileChooser
      .getExtensionFilters()
      .addAll(
        new FileChooser.ExtensionFilter(
          resourceBundle.getString("applications-file"),
          "*.bin"
        )
      );

    final var file = Optional.ofNullable(fileChooser.showOpenDialog(stage));

    if (file.isEmpty()) {
      return;
    }

    final var jobExecution = importerExporter.importApplications(file.get());

    final Alert alert;
    if (jobExecution.getExitStatus().equals(ExitStatus.COMPLETED)) {
      alert =
        new Alert(
          AlertType.INFORMATION,
          resourceBundle.getString("applications-imported")
        );
    } else {
      alert =
        new Alert(
          AlertType.ERROR,
          resourceBundle
            .getString("applications-not-imported")
            .formatted(getJobThrowableMessages(jobExecution))
        );
    }
    alert.setTitle(resourceBundle.getString("import-result"));
    alert.setHeaderText(resourceBundle.getString("import-result"));
    alert.showAndWait();

    reloadApplicationItems();
  }

  @NotNull
  private String getJobThrowableMessages(
    final @NotNull JobExecution jobExecution
  ) {
    return jobExecution
      .getFailureExceptions()
      .stream()
      .map(Throwable::getLocalizedMessage)
      .collect(Collectors.joining("\n"));
  }

  @FXML
  public void exportButtonAction(final ActionEvent event)
    throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, InvalidJobParametersException, JobRestartException {
    var fileChooser = new FileChooser();
    fileChooser.setTitle(resourceBundle.getString("select-where-export"));
    fileChooser
      .getExtensionFilters()
      .addAll(
        new FileChooser.ExtensionFilter(
          resourceBundle.getString("applications-file"),
          "*.bin"
        )
      );

    final var file = Optional.ofNullable(fileChooser.showSaveDialog(stage));

    if (file.isEmpty()) {
      return;
    }

    final var jobExecution = importerExporter.exportApplications(file.get());

    final Alert alert;
    if (jobExecution.getExitStatus().equals(ExitStatus.COMPLETED)) {
      alert =
        new Alert(
          AlertType.INFORMATION,
          resourceBundle.getString("applications-exported-successfully")
        );
    } else {
      alert =
        new Alert(
          AlertType.ERROR,
          resourceBundle
            .getString("applications-not-exported")
            .formatted(getJobThrowableMessages(jobExecution))
        );
    }
    alert.setTitle(resourceBundle.getString("export-result"));
    alert.setHeaderText(resourceBundle.getString("export-result"));
    alert.showAndWait();
  }

  public void open(final Stage owner) throws IOException {
    stage = new Stage();
    final var fxmlLoader = new FXMLLoader(
      context.getResource("classpath:applications.fxml").getURL()
    );
    fxmlLoader.setControllerFactory(context::getBean);
    resourceBundle = ResourceBundle.getBundle("applications");
    fxmlLoader.setResources(resourceBundle);
    stage.setMinWidth(WIDTH + 16.);
    stage.setMinHeight(HEIGHT + 39.);
    stage.setScene(new Scene(fxmlLoader.load(), WIDTH, HEIGHT));
    stage.initOwner(owner);
    stage.setTitle(resourceBundle.getString("applications-list"));
    stage.initModality(Modality.WINDOW_MODAL);
    stage.setOnCloseRequest(this::stageCloseRequest);
    DarculaFX.applyDarculaStyle(stage.getScene());
    //    new JMetro(Style.DARK).setScene(stage.getScene());
    stage.show();
  }

  private void stageCloseRequest(final WindowEvent event) {
    if (
      applicationItems
        .stream()
        .map(ApplicationRow::getId)
        .allMatch(id -> id != null && id != 0)
    ) {
      return;
    }

    var alert = new Alert(
      AlertType.CONFIRMATION,
      resourceBundle.getString("unsaved-applications-left"),
      ButtonTypes.YES,
      ButtonTypes.NO
    );
    alert.setTitle(AlertBundle.CONFIRMATION);
    alert.setHeaderText(resourceBundle.getString("exit-confirmation"));

    final var answer = alert.showAndWait();
    if (answer.isPresent() && answer.get().equals(ButtonTypes.NO)) {
      event.consume();
    }
  }

  @SuppressWarnings("unused")
  @EqualsAndHashCode
  public static class ApplicationRow {

    private final SimpleStringProperty name;
    private final SimpleObjectProperty<ApplicationType> applicationType;
    private final SimpleStringProperty siteUrl;
    private final SimpleStringProperty updateUrl;
    private final SimpleStringProperty currentVersion;
    private final SimpleObjectProperty<LocalDateTime> lastUpdated;
    private final SimpleBooleanProperty ignored;

    @Getter
    @Setter
    private Long id;

    public ApplicationRow(@NotNull final Application application) {
      id = application.getId();
      name = new SimpleStringProperty(application.getName());
      applicationType =
        new SimpleObjectProperty<>(application.getApplicationType());
      siteUrl = new SimpleStringProperty(application.getSiteUrl());
      updateUrl = new SimpleStringProperty(application.getUpdateUrl());
      currentVersion =
        new SimpleStringProperty(application.getCurrentVersion());
      lastUpdated = new SimpleObjectProperty<>(application.getLastUpdate());
      ignored = new SimpleBooleanProperty(application.isIgnored());
    }

    public ApplicationRow() {
      this(new Application());
    }

    public LocalDateTime getLastUpdated() {
      return lastUpdated.get();
    }

    public SimpleObjectProperty<LocalDateTime> lastUpdatedProperty() {
      return lastUpdated;
    }

    public String getName() {
      return name.get();
    }

    public void setName(String name) {
      this.name.set(name);
    }

    public SimpleStringProperty nameProperty() {
      return name;
    }

    public ApplicationType getApplicationType() {
      return applicationType.get();
    }

    public void setApplicationType(ApplicationType applicationType) {
      this.applicationType.set(applicationType);
    }

    public SimpleObjectProperty<ApplicationType> applicationTypeProperty() {
      return applicationType;
    }

    public String getSiteUrl() {
      return siteUrl.get();
    }

    public void setSiteUrl(String siteUrl) {
      this.siteUrl.set(siteUrl);
    }

    public SimpleStringProperty siteUrlProperty() {
      return siteUrl;
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

    public String getCurrentVersion() {
      return currentVersion.get();
    }

    public void setCurrentVersion(String currentVersion) {
      this.currentVersion.set(currentVersion);
    }

    public SimpleStringProperty currentVersionProperty() {
      return currentVersion;
    }

    public boolean isIgnored() {
      return ignored.get();
    }

    public void setIgnored(boolean ignored) {
      this.ignored.set(ignored);
    }

    public SimpleBooleanProperty ignoredProperty() {
      return ignored;
    }
  }
}
