package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.CAMTASIA;

import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import java.io.IOException;

@ApplicationType(CAMTASIA)
public class CamtasiaUpdateChecker extends UpdateChecker {

  public CamtasiaUpdateChecker(
    final String siteUrl,
    final String currentVersion
  ) {
    super(siteUrl, currentVersion);
  }

  @Override
  public void checkUpdate() throws IOException {
    final String version = getJsoupConnectionInstance()
      .get()
      .selectFirst("div > h3:nth-child(1)")
      .text();

    setNewVersion(version.substring(version.lastIndexOf(' ') + 1));
  }
}
