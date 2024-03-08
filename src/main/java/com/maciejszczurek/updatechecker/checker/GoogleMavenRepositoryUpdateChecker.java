package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.GOOGLE_MAVEN_REPO;

import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import com.maciejszczurek.updatechecker.util.UrlBuilder;
import java.io.IOException;
import java.util.Arrays;

@ApplicationType(GOOGLE_MAVEN_REPO)
public class GoogleMavenRepositoryUpdateChecker extends UpdateChecker {

  public GoogleMavenRepositoryUpdateChecker(
    final String siteUrl,
    final String currentVersion
  ) {
    super(siteUrl, currentVersion);
  }

  @Override
  public void checkUpdate() throws IOException {
    final var packageIds = UrlBuilder.build(getSiteUrl()).getRef().split(":");

    final var versions = Arrays
      .stream(
        getJsoupConnectionInstance(
          "https://dl.google.com/android/maven2/%s/group-index.xml".formatted(
              packageIds[0].replace('.', '/')
            )
        )
          .get()
          .child(0)
          .select(packageIds[1])
          .attr("versions")
          .split(",")
      )
      .filter(version -> !version.contains("-"))
      .toList();
    setNewVersion(versions.get(versions.size() - 1));
  }
}
