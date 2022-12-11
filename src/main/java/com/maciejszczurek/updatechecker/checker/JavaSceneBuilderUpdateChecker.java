package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.JAVA_SCENE_BUILDER;

import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import java.io.IOException;

@ApplicationType(JAVA_SCENE_BUILDER)
public class JavaSceneBuilderUpdateChecker extends UpdateChecker {

  public JavaSceneBuilderUpdateChecker(
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
        .select("#page-1025 > div > p:nth-child(11) > strong:nth-child(1)")
        .html()
    );
  }
}
