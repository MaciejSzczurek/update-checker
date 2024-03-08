package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.PIP;

import com.maciejszczurek.updatechecker.application.NewVersionNotFoundException;
import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import com.maciejszczurek.updatechecker.util.UpdateCheckerUtils;
import com.maciejszczurek.updatechecker.util.UrlBuilder;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

@ApplicationType(PIP)
public class PipUpdateChecker extends UpdateChecker {

  public PipUpdateChecker(final String siteUrl, final String currentVersion) {
    super(siteUrl, currentVersion);
  }

  @Override
  public void checkUpdate() throws IOException, InterruptedException {
    final var siteUrl = UrlBuilder.build(getSiteUrl());
    final var path = new ArrayList<>(
      Arrays.asList(siteUrl.getPath().split("/"))
    );
    path.set(1, "pypi");

    final var lastElement = path.size() - 1;
    if (path.get(lastElement).isEmpty()) {
      path.set(lastElement, "json");
    } else {
      path.add("json");
    }

    final var version = UpdateCheckerUtils
      .readTree(
        UrlBuilder.build(
          siteUrl.getProtocol(),
          siteUrl.getHost(),
          String.join("/", path)
        )
      )
      .get("info")
      .get("version")
      .asText();

    if (version.isEmpty()) {
      throw new NewVersionNotFoundException("New version is empty.");
    }

    setNewVersion(version);
  }
}
