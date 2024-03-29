package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.CCLEANER;

import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import java.io.IOException;

@ApplicationType(CCLEANER)
public class CCleanerUpdateChecker extends UpdateChecker {

  public CCleanerUpdateChecker(
    final String siteUrl,
    final String currentVersion
  ) {
    super(siteUrl, currentVersion);
  }

  @Override
  public void checkUpdate() throws IOException {
    setNewVersion(
      getJsoupConnectionInstance()
        .get()
        .selectFirst(
          "span[data-qa-id='version-history__latest-version']"
        )
        .text()
        .replace("v", "")
    );
  }
}
