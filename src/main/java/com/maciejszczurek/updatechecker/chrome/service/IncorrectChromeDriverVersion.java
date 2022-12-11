package com.maciejszczurek.updatechecker.chrome.service;

public class IncorrectChromeDriverVersion extends RuntimeException {

  public IncorrectChromeDriverVersion() {
    super(
      "The driver version is incompatible with the installed version of Google Chrome."
    );
  }
}
