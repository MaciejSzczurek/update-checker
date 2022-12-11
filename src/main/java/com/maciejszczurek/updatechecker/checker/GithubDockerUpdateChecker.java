package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.GITHUB_DOCKER;

import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import java.io.IOException;

@ApplicationType(GITHUB_DOCKER)
public class GithubDockerUpdateChecker extends UpdateChecker {

  public GithubDockerUpdateChecker(
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
        .select("a.Label--success")
        .parents()
        .get(1)
        .select("span.color-fg-muted")
        .html()
        .replace("sha256:", "")
        .substring(0, 12)
    );
  }
}
