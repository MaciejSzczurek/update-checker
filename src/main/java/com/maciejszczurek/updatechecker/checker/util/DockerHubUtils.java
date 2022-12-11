package com.maciejszczurek.updatechecker.checker.util;

import java.net.URI;
import java.util.stream.Stream;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class DockerHubUtils {

  public String extractTagFromQuery(@NotNull final URI uri, final String name) {
    return Stream
      .of(uri.getQuery().split("&"))
      .map(s -> s.split("="))
      .filter(strings -> strings.length == 2)
      .filter(strings -> strings[0].equals(name))
      .findFirst()
      .orElseThrow()[1];
  }
}
