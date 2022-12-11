package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.GITHUB_FILE;

import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import com.maciejszczurek.updatechecker.util.UpdateCheckerUtils;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.stream.Collectors;

@ApplicationType(GITHUB_FILE)
public class GithubFileUpdateChecker extends UpdateChecker {

  public GithubFileUpdateChecker(
    final String siteUrl,
    final String currentVersion
  ) {
    super(siteUrl, currentVersion);
  }

  @Override
  public void checkUpdate() throws IOException {
    final var url = new URL(getSiteUrl());
    final var path = Arrays.asList(url.getPath().split("/"));

    var version = "";

    try {
      version =
        UpdateCheckerUtils
          .readTree(
            new URL(
              url.getProtocol(),
              "api.github.com",
              "/repos/%s/%s/commits?path=%s".formatted(
                  path.get(1),
                  path.get(2),
                  path.stream().skip(5).collect(Collectors.joining("/"))
                )
            )
          )
          .get(0)
          .get("sha")
          .asText("");
    } catch (IOException e) {
      // ignored
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    setNewVersion(
      version.isEmpty()
        ? getJsoupConnectionInstance()
          .get()
          .selectFirst("a.text-small.text-mono.Link--secondary")
          .text()
        : version.substring(0, 7)
    );
  }
}
