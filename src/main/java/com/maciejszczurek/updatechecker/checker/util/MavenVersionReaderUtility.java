package com.maciejszczurek.updatechecker.checker.util;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Document;

@UtilityClass
public class MavenVersionReaderUtility {

  public String readVersion(@NotNull final Document document) {
    var element = document.selectFirst("metadata > versioning > release");

    return element == null
      ? document
        .select("metadata > versioning > versions > version")
        .last()
        .text()
      : element.text();
  }
}
