package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.NODEJS;

import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import java.io.IOException;

@ApplicationType(NODEJS)
public class NodejsUpdateChecker extends UpdateChecker {

  public NodejsUpdateChecker(
    final String siteUrl,
    final String currentVersion
  ) {
    super(siteUrl, currentVersion);
  }

  @Override
  public void checkUpdate() throws IOException, InterruptedException {
    var versionsData = getJsoupConnectionInstance()
      .get()
      .select("script")
      .html();
    if (getSiteUrl().endsWith("/current")) {
      versionsData = versionsData.substring(
        versionsData.indexOf("\\\"status\\\":\\\"Current\\\"")
      );
    } else {
      var ltsVersionIndexLocation = versionsData.indexOf(
        "\\\"status\\\":\\\"Active LTS\\\""
      );
      if (ltsVersionIndexLocation == -1) {
        ltsVersionIndexLocation = versionsData.indexOf(
          "\\\"status\\\":\\\"Maintenance LTS\\\""
        );
      }
      versionsData = versionsData.substring(ltsVersionIndexLocation);
    }
    versionsData = versionsData.substring(
      versionsData.indexOf("\\\"version\\\":\\\"") + 14
    );
    setNewVersion(versionsData.substring(0, versionsData.indexOf("\\\"")));
  }
}
