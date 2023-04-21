package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.JAVA;

import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import java.io.IOException;

@ApplicationType(JAVA)
public class JavaUpdateChecker extends UpdateChecker {

  public JavaUpdateChecker(final String siteUrl, final String currentVersion) {
    super(siteUrl, currentVersion);
  }

  @Override
  public void checkUpdate() throws IOException {
    final var document = getJsoupConnectionInstance().get();

    setNewVersion(
      document
        .select(
          (
            getSiteUrl().contains("#")
              ? getSiteUrl().substring(getSiteUrl().indexOf("#")) + " > "
              : ""
          ) +
          "section.rc24:nth-child(1) > div > h3"
        )
        .stream()
        .findFirst()
        .orElse(
          document.selectFirst(
            (
              getSiteUrl().contains("#")
                ? getSiteUrl().substring(getSiteUrl().indexOf("#")) + " > "
                : ""
            ) +
            "section.rc24:nth-child(1) > div > h2"
          )
        )
        .text()
        .replace("Java SE Development Kit ", "")
        .replace("JDK Development Kit ", "")
        .replace(" downloads", "")
    );
  }
}
