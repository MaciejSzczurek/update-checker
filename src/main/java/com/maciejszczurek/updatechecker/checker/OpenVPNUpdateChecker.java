package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.OPEN_VPN;

import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import java.io.IOException;

@ApplicationType(OPEN_VPN)
public class OpenVPNUpdateChecker extends UpdateChecker {

  public OpenVPNUpdateChecker(String siteUrl, String currentVersion) {
    super(siteUrl, currentVersion);
  }

  @Override
  public void checkUpdate() throws IOException, InterruptedException {
    var version = getJsoupConnectionInstance()
      .get()
      .select("div.card:nth-child(1) > div.card-header > h5 > button")
      .text()
      .strip()
      .replace("OpenVPN ", "");
    setNewVersion(version.substring(0, version.indexOf(" --")));
  }
}
