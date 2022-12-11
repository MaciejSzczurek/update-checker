package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.TORTOISE_GIT;

import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import java.io.IOException;

@ApplicationType(TORTOISE_GIT)
public class TortoiseGitUpdateChecker extends UpdateChecker {

  public TortoiseGitUpdateChecker(
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
        .select("#wrapper > div:nth-child(3) > div > p:nth-child(2) > strong")
        .html()
        .replace("The current stable version is: ", "")
    );
  }
}
