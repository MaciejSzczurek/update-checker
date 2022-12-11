package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.UBUNTU;

import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import java.io.IOException;

@ApplicationType(UBUNTU)
public class UbuntuUpdateChecker extends UpdateChecker {

  public UbuntuUpdateChecker(
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
        .selectFirst(
          "#maincol > div:nth-child(2) > div.col-md-8.col-xs-12 > table > tbody > " +
          "tr:nth-child(3) > td:nth-child(2)"
        )
        .text()
    );
  }
}
