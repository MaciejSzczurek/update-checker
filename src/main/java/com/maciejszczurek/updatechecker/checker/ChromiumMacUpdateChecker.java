package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.CHROMIUM_MAC;

import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import java.io.IOException;

@ApplicationType(CHROMIUM_MAC)
public class ChromiumMacUpdateChecker extends UpdateChecker {

  public ChromiumMacUpdateChecker(
    final String siteUrl,
    final String currentVersion
  ) {
    super(siteUrl, currentVersion);
  }

  @Override
  public void checkUpdate() throws IOException {
    final String version = getJsoupConnectionInstance()
      .get()
      .selectFirst("#mac-stable-sync-marmaduke > details > summary > small")
      .text();

    setNewVersion(version.substring(0, version.indexOf(' ')));
  }
}
