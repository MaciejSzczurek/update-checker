package com.maciejszczurek.updatechecker.chrome.service;

public class ChromeDriverVersionNotFoundException extends RuntimeException {

  public ChromeDriverVersionNotFoundException() {
    super("Chrome Driver version was not found.");
  }
}
