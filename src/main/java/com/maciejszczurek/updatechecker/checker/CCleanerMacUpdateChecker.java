package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.CCLEANER_MAC;

import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import java.io.IOException;

@ApplicationType(CCLEANER_MAC)
public class CCleanerMacUpdateChecker extends UpdateChecker {

  public CCleanerMacUpdateChecker(
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
        .select("#GTM__link--CC-mac-download-footer")
        .attr("href")
        .replace("https:", "")
        .replace("http:", "")
        .replace("//download.ccleaner.com/mac/CCMacSetup", "")
        .replace(".dmg", "")
    );
  }
}
