package com.maciejszczurek.updatechecker.javafx;

import java.util.ResourceBundle;
import lombok.experimental.UtilityClass;

@UtilityClass
public class AlertBundle {

  private final ResourceBundle BUNDLE = ResourceBundle.getBundle("javafx");
  public final String CONFIRMATION = BUNDLE.getString("alert.confirmation");
  public final String ERROR = BUNDLE.getString("alert.error");
}
