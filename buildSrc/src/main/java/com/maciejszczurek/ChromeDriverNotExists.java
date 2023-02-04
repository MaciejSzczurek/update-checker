package com.maciejszczurek;

public class ChromeDriverNotExists extends RuntimeException {

  public ChromeDriverNotExists() {
    super("ChromeDriver does not exists.");
  }
}
