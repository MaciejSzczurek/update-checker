package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.VLC;

import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import java.io.IOException;

@ApplicationType(VLC)
public class VlcUpdateChecker extends UpdateChecker {

  public VlcUpdateChecker(final String siteUrl, final String currentVersion) {
    super(siteUrl, currentVersion);
  }

  @Override
  public void checkUpdate() throws IOException {
    setNewVersion(
      getJsoupConnectionInstance()
        .get()
        .select("#downloadVersion")
        .html()
        .trim()
    );
  }
}
