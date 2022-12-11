package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.PACKAGIST;

import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import java.io.IOException;

@ApplicationType(PACKAGIST)
public class PackagistUpdateChecker extends UpdateChecker {

  public PackagistUpdateChecker(
    final String siteUrl,
    final String currentVersion
  ) {
    super(siteUrl, currentVersion);
  }

  @Override
  public void checkUpdate() throws IOException {
    String text = getJsoupConnectionInstance()
      .get()
      .select(
        "body > section > section:nth-child(3) > section > div.row > div > " +
        "div.row.versions-section > div.version-details.col-md-9 > " +
        "div.title > span.version-number"
      )
      .html();

    if (text.charAt(0) == 'v') {
      text = text.substring(1);
    }

    setNewVersion(text);
  }
}
