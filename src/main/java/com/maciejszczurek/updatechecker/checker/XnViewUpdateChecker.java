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
    setNewVersion(
      getJsoupConnectionInstance()
        .get()
        .select("#downloads > p.h5.mt-3 > strong")
        .html()
        .replace("XnView MP ", "")
    );
  }
}
