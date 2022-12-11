package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.FRESH_TOMATO;

import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import java.io.IOException;

@ApplicationType(FRESH_TOMATO)
public class FreshTomatoUpdateChecker extends UpdateChecker {

  public FreshTomatoUpdateChecker(
    final String siteUrl,
    final String currentVersion
  ) {
    super(siteUrl, currentVersion);
  }

  @Override
  public void checkUpdate() throws IOException {
    final String version = getJsoupConnectionInstance()
      .get()
      .select("body > div > div.container > div.left > div:nth-child(13)")
      .text()
      .replace("Stable: ", "");

    setNewVersion(version.substring(0, version.indexOf(" (")));
  }
}
