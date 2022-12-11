package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.GCAM;

import com.maciejszczurek.updatechecker.application.NewVersionNotFoundException;
import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import java.io.IOException;

@ApplicationType(GCAM)
public class GcamUpdateChecker extends UpdateChecker {

  private static final String APK_PREFIX = "MGC_";
  private static final String APK_SUFFIX = "_ENG.apk";

  public GcamUpdateChecker(final String siteUrl, final String currentVersion) {
    super(siteUrl, currentVersion);
  }

  @Override
  public void checkUpdate() throws IOException {
    final var apkFilename = getJsoupConnectionInstance()
      .get()
      .select(
        "#content > article > div.contentarticle > ul:nth-child(3) > li > a"
      )
      .parallelStream()
      .filter(element -> element.text().endsWith(APK_SUFFIX))
      .findFirst()
      .orElseThrow(() ->
        new NewVersionNotFoundException(
          "Cannot find new version for Google Camera"
        )
      )
      .text()
      .replace(APK_PREFIX, "");

    setNewVersion(apkFilename.substring(0, apkFilename.indexOf('_')));
  }
}
