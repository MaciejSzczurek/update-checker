package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.LOMBOK_EDGE;

import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import java.io.IOException;

@ApplicationType(LOMBOK_EDGE)
public class LombokEdgeUpdateChecker extends UpdateChecker {

  public LombokEdgeUpdateChecker(
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
        .select(".versionInfo:nth-child(2)")
        .html()
        .trim()
        .replace("version: ", "")
    );
  }
}
