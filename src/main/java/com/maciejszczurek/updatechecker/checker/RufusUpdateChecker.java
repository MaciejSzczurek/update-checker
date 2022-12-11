package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.RUFUS;

import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import java.io.IOException;

@ApplicationType(RUFUS)
public class RufusUpdateChecker extends UpdateChecker {

  public RufusUpdateChecker(final String siteUrl, final String currentVersion) {
    super(siteUrl, currentVersion);
  }

  @Override
  public void checkUpdate() throws IOException {
    setNewVersion(
      getJsoupConnectionInstance()
        .get()
        .select("li:nth-child(1) > span:nth-child(1) > b > a")
        .text()
        .replace("Rufus ", "")
    );
  }
}
