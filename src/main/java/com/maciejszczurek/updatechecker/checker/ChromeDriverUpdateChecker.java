package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.CHROME_DRIVER;

import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import com.maciejszczurek.updatechecker.util.UpdateCheckerUtils;
import java.io.IOException;
import java.net.URI;

@ApplicationType(CHROME_DRIVER)
public class ChromeDriverUpdateChecker extends UpdateChecker {

  public ChromeDriverUpdateChecker(
    final String siteUrl,
    final String currentVersion
  ) {
    super(siteUrl, currentVersion);
  }

  @Override
  public void checkUpdate() throws IOException {
    setNewVersion(
      UpdateCheckerUtils
        .getObjectMapper()
        .readTree(URI.create(getSiteUrl()).toURL())
        .get("channels")
        .get("Stable")
        .get("version")
        .textValue()
    );
  }
}
