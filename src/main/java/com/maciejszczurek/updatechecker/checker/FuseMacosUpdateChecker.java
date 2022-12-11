package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.FUSE_MACOS;

import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import java.io.IOException;

@ApplicationType(FUSE_MACOS)
public class FuseMacosUpdateChecker extends UpdateChecker {

  public FuseMacosUpdateChecker(
    final String siteUrl,
    final String currentVersion
  ) {
    super(siteUrl, currentVersion);
  }

  @Override
  public void checkUpdate() throws IOException {
    final String url = getJsoupConnectionInstance()
      .get()
      .selectFirst("#osxfuse > a")
      .attr("href");

    setNewVersion(
      url
        .substring(url.lastIndexOf('/') + 1)
        .replace("osxfuse-", "")
        .replace(".dmg", "")
    );
  }
}
