package com.maciejszczurek.updatechecker.application;

import java.io.IOException;

public class NewVersionNotFoundException extends IOException {

  public NewVersionNotFoundException(final String message) {
    super(message);
  }
}
