package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.GRADLE_PLUGIN;

import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import com.maciejszczurek.updatechecker.checker.util.MavenVersionReaderUtility;
import java.io.IOException;

@ApplicationType(GRADLE_PLUGIN)
public class GradlePluginUpdateChecker extends UpdateChecker {

  public GradlePluginUpdateChecker(
    final String siteUrl,
    final String currentVersion
  ) {
    super(siteUrl, currentVersion);
  }

  @Override
  public void checkUpdate() throws IOException {
    var id = getSiteUrl().replace("https://plugins.gradle.org/plugin/", "");

    setNewVersion(
      MavenVersionReaderUtility.readVersion(
        getJsoupConnectionInstance(
          "https://plugins.gradle.org/m2/%s/%s.gradle.plugin/maven-metadata.xml".formatted(
              id.replace('.', '/'),
              id
            )
        )
          .get()
      )
    );
  }
}
