package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.AMD_DRIVERS;

import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import java.io.IOException;

@ApplicationType(AMD_DRIVERS)
public class AMDDriverUpdateChecker extends UpdateChecker {

  public AMDDriverUpdateChecker(
    final String siteUrl,
    final String currentVersion
  ) {
    super(siteUrl, currentVersion);
  }

  @Override
  public void checkUpdate() throws IOException {
    final String text = getJsoupConnectionInstance().get().html();
    final int revisionIndex =
      text.indexOf("\\\"RevisionNumberOWSTEXT\\\":\\\"") + 28;

    setNewVersion(
      text.substring(revisionIndex, text.indexOf("\\\",", revisionIndex))
    );
  }
}
