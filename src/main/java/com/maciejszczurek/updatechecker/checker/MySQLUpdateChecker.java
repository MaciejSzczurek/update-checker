package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.MYSQL;

import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import java.io.IOException;

@ApplicationType(MYSQL)
public class MySQLUpdateChecker extends UpdateChecker {

  public MySQLUpdateChecker(final String siteUrl, final String currentVersion) {
    super(siteUrl, currentVersion);
  }

  @Override
  public void checkUpdate() throws IOException {
    setNewVersion(
      getJsoupConnectionInstance()
        .get()
        .selectFirst("#ga > h1")
        .text()
        .replace("MySQL Installer ", "")
    );
  }
}
