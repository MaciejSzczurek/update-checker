package com.maciejszczurek.updatechecker.checker;

public class UserAgentGenerationException extends RuntimeException {

  public UserAgentGenerationException(final Throwable cause) {
    super("User-Agent was not generated.", cause);
  }
}
