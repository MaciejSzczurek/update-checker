package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.PECL;

import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import java.io.IOException;

@ApplicationType(PECL)
public class PeclUpdateChecker extends UpdateChecker {

  public PeclUpdateChecker(final String siteUrl, final String currentVersion) {
    super(siteUrl, currentVersion);
  }

  @Override
  public void checkUpdate() throws IOException {
    setNewVersion(
      getJsoupConnectionInstance()
        .get()
        .select(
          "body > table.middle > tbody > tr > td.content > table:nth-child(8) > tbody > tr > td > " +
          "table > tbody > tr:nth-child(3) > th > a"
        )
        .text()
    );
  }
}
