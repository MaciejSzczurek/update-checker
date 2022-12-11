package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.DRIVER_GENIUS;

import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import java.io.IOException;

@ApplicationType(DRIVER_GENIUS)
public class DriverGeniusUpdateChecker extends UpdateChecker {

  public DriverGeniusUpdateChecker(
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
        .select("body > div.k > div.Download_Top > p:nth-child(2)")
        .text()
        .replace("Version: ", "")
    );
  }
}
