package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.CYGWIN_SETUP;

import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import java.io.IOException;

@ApplicationType(CYGWIN_SETUP)
public class CygwinSetupUpdateChecker extends UpdateChecker {

  public CygwinSetupUpdateChecker(
    final String siteUrl,
    final String currentVersion
  ) {
    super(siteUrl, currentVersion);
  }

  @Override
  public void checkUpdate() throws IOException {
    final String body = getJsoupConnectionInstance()
      .get()
      .select("body")
      .html();

    setNewVersion(
      body.substring(body.indexOf("setup-version: ") + 15, body.indexOf(" @ "))
    );
  }
}
