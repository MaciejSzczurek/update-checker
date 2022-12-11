package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.JETBRAINS;

import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import com.maciejszczurek.updatechecker.util.UpdateCheckerUtils;
import java.io.IOException;
import java.net.URL;

@ApplicationType(JETBRAINS)
public class JetBrainsUpdateChecker extends UpdateChecker {

  public JetBrainsUpdateChecker(
    final String siteUrl,
    final String currentVersion
  ) {
    super(siteUrl, currentVersion);
  }

  @Override
  public void checkUpdate() throws IOException, InterruptedException {
    setNewVersion(
      UpdateCheckerUtils
        .readTree(new URL(getSiteUrl()))
        .findValue("version")
        .asText()
    );
  }
}
