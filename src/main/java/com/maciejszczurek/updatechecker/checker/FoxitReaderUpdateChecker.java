package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.FOXIT_READER;

import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import com.maciejszczurek.updatechecker.util.UpdateCheckerUtils;
import java.io.IOException;
import java.net.URL;

@ApplicationType(FOXIT_READER)
public class FoxitReaderUpdateChecker extends UpdateChecker {

  public FoxitReaderUpdateChecker(
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
        .get("package_info")
        .get("version")
        .get(0)
        .asText()
    );
  }
}
