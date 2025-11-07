package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.GIT_SCM;

import com.maciejszczurek.updatechecker.application.NewVersionNotFoundException;
import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import java.io.IOException;
import java.util.Optional;

@ApplicationType(GIT_SCM)
public class GitScmUpdateChecker extends UpdateChecker {

  public GitScmUpdateChecker(
    final String siteUrl,
    final String currentVersion
  ) {
    super(siteUrl, currentVersion);
  }

  @Override
  public void checkUpdate() throws IOException {
    setNewVersion(
      Optional.ofNullable(
        getJsoupConnectionInstance().get().selectFirst("#auto-download-version")
      )
        .orElseThrow(() ->
          new NewVersionNotFoundException(
            "Selector for git version is not found"
          )
        )
        .text()
    );
  }
}
