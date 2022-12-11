package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.WINDOWS_REPAIR;

import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import java.io.IOException;

@ApplicationType(WINDOWS_REPAIR)
public class WindowsRepairUpdateChecker extends UpdateChecker {

  public WindowsRepairUpdateChecker(
    final String siteUrl,
    final String currentVersion
  ) {
    super(siteUrl, currentVersion);
  }

  @Override
  public void checkUpdate() throws IOException {
    final String text = getJsoupConnectionInstance()
      .get()
      .select("#content > div.content")
      .text();
    final int versionIndex = text.indexOf("Version ");

    setNewVersion(
      text.substring(versionIndex + 8, text.indexOf(" (", versionIndex))
    );
  }
}
