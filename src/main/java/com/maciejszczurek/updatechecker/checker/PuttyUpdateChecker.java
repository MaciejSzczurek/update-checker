package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.PUTTY;

import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import java.io.IOException;

@ApplicationType(PUTTY)
public class PuttyUpdateChecker extends UpdateChecker {

  public PuttyUpdateChecker(final String siteUrl, final String currentVersion) {
    super(siteUrl, currentVersion);
  }

  @Override
  public void checkUpdate() throws IOException {
    final String header = getJsoupConnectionInstance()
      .get()
      .selectFirst("body > h1")
      .text();

    setNewVersion(
      header.substring(header.indexOf('(') + 1, header.indexOf(')'))
    );
  }
}
