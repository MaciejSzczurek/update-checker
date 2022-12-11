package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.CCENHANCER;

import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import java.io.IOException;

@ApplicationType(CCENHANCER)
public class CCEnhancerUpdateChecker extends UpdateChecker {

  public CCEnhancerUpdateChecker(
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
          "#post-970 > div > div > table > tbody > tr:nth-child(2) > td:nth-child(2)"
        )
        .html()
    );
  }
}
