package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.NAVICAT;

import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import java.io.IOException;

@ApplicationType(NAVICAT)
public class NavicatUpdateChecker extends UpdateChecker {

  public NavicatUpdateChecker(
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
        .select(
          "body > div > div.main-article > div > div > div > div:nth-child(4) > " +
          "table:nth-child(1) > tbody > tr:nth-child(1) > td > div.note-title > b"
        )
        .text()
        .replace("Navicat Premium (Windows) version ", "")
    );
  }
}
