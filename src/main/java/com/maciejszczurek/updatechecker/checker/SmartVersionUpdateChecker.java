package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.SMART_VERSION;

import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import java.io.IOException;

@ApplicationType(SMART_VERSION)
public class SmartVersionUpdateChecker extends UpdateChecker {

  public SmartVersionUpdateChecker(
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
        .select("body > table > tbody > tr > td:nth-child(3) > h2:nth-child(2)")
        .text()
        .replace("SmartVersion ", "")
    );
  }
}
