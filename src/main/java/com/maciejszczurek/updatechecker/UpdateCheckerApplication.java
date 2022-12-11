package com.maciejszczurek.updatechecker;

import javafx.application.Application;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableBatchProcessing
public class UpdateCheckerApplication {

  public static void main(String[] args) {
    Application.launch(UpdateCheckerFxApplication.class, args);
  }
}
