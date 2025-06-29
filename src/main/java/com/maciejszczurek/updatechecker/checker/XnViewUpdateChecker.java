package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.XN_VIEW;

import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import java.io.IOException;

@ApplicationType(XN_VIEW)
public class XnViewUpdateChecker extends UpdateChecker {

  public XnViewUpdateChecker(
    final String siteUrl,
    final String currentVersion
  ) {
    super(siteUrl, currentVersion);
  }

  @Override
  public void checkUpdate() throws IOException {
    var anchor = getJsoupConnectionInstance()
      .get()
      .select(
        "#downloads > table > tbody > tr:nth-child(11) > td:nth-child(2) > a"
      );
    setNewVersion(
      !anchor.isEmpty() ? anchor.first().text().replace("XnView MP ", "") : ""
    );
  }
}
