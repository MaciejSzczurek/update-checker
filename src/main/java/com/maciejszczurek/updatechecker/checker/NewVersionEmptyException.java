package com.maciejszczurek.updatechecker.checker;

import java.io.IOException;

public class NewVersionEmptyException extends IOException {

  public NewVersionEmptyException() {
    super("New version is empty.");
  }
}
