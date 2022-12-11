package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.ADOBE_DIGITAL_EDITIONS;

import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import java.io.IOException;

@ApplicationType(ADOBE_DIGITAL_EDITIONS)
public class AdobeDigitalEditionsUpdateChecker extends UpdateChecker {

  public AdobeDigitalEditionsUpdateChecker(
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
        .select("#general > div > div:nth-child(1) > div > h2 > b")
        .html()
        .replace("Adobe Digital Editions ", "")
        .replace(" Installers", "")
    );
  }
}
