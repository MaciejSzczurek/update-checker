package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.NANO;

import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import java.io.IOException;

@ApplicationType(NANO)
public class NanoUpdateChecker extends UpdateChecker {

  public NanoUpdateChecker(final String siteUrl, final String currentVersion) {
    super(siteUrl, currentVersion);
  }

  @Override
  public void checkUpdate() throws IOException {
    setNewVersion(
      getJsoupConnectionInstance()
        .get()
        .select(
          "body > center:nth-child(5) > table > tbody > tr:nth-child(2) > td:nth-child(2) > a"
        )
        .text()
        .replace("nano-", "")
        .replace(".tar.xz", "")
    );
  }
}
