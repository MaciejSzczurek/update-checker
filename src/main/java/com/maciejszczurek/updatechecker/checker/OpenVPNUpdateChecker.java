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
      .select(
        "#community_download_banner_btn > span"
      )
      .text()
      .strip()
      .replace("Download OpenVPN-", "");
    setNewVersion(version.substring(0, version.indexOf("-")));
  }
}
