package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.S3BROWSER;

import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import java.io.IOException;

@ApplicationType(S3BROWSER)
public class S3BrowserUpdateChecker extends UpdateChecker {

  public S3BrowserUpdateChecker(
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
        .selectFirst("#download > div.download_caption")
        .text()
        .replace("S3 Browser Version ", "")
    );
  }
}
