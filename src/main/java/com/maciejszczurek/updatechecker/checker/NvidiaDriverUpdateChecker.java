package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.NVIDIA_DRIVER;

import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import com.maciejszczurek.updatechecker.util.UpdateCheckerUtils;
import java.io.IOException;
import java.net.URI;

@ApplicationType(NVIDIA_DRIVER)
public class NvidiaDriverUpdateChecker extends UpdateChecker {

  public NvidiaDriverUpdateChecker(
    final String siteUrl,
    final String currentVersion
  ) {
    super(siteUrl, currentVersion);
  }

  @Override
  public void checkUpdate() throws IOException, InterruptedException {
    setNewVersion(
      UpdateCheckerUtils.readTree(URI.create(getSiteUrl()).toURL())
        .get("IDS")
        .get(0)
        .get("downloadInfo")
        .get("Version")
        .asText()
    );
  }
}
