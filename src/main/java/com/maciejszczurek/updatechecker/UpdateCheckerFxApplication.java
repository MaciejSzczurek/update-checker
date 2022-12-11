package com.maciejszczurek.updatechecker;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

public class UpdateCheckerFxApplication extends Application {

  private ConfigurableApplicationContext context;

  @Override
  public void stop() {
    context.close();
    Platform.exit();
  }

  @Override
  public void init() {
    final ApplicationContextInitializer<GenericApplicationContext> initializer = genericApplicationContext ->
      genericApplicationContext.registerBean(
        Application.class,
        () -> UpdateCheckerFxApplication.this
      );

    context =
      new SpringApplicationBuilder()
        .sources(UpdateCheckerApplication.class)
        .initializers(initializer)
        .build()
        .run(getParameters().getRaw().toArray(String[]::new));
  }

  @Override
  public void start(final Stage primaryStage) {
    context.publishEvent(new PrimaryStageReadyEvent(primaryStage));
  }

  public static class PrimaryStageReadyEvent extends ApplicationEvent {

    public PrimaryStageReadyEvent(final Stage source) {
      super(source);
    }

    public Stage getStage() {
      return (Stage) getSource();
    }
  }
}
