package com.maciejszczurek.updatechecker.javafx;

import static javafx.scene.control.ButtonBar.ButtonData;

import java.util.ResourceBundle;
import javafx.scene.control.ButtonType;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ButtonTypes {

  private final ResourceBundle BUNDLE = ResourceBundle.getBundle("javafx");
  public final ButtonType YES = new ButtonType(
    BUNDLE.getString("button.yes"),
    ButtonData.YES
  );
  public final ButtonType NO = new ButtonType(
    BUNDLE.getString("button.no"),
    ButtonData.NO
  );
}
