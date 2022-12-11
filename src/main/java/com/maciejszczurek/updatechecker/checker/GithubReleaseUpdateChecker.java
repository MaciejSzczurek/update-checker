package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.GITHUB_RELEASE;

import com.fasterxml.jackson.core.type.TypeReference;
import com.maciejszczurek.updatechecker.application.NewVersionNotFoundException;
import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import com.maciejszczurek.updatechecker.util.UpdateCheckerUtils;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import org.jsoup.nodes.Element;

@ApplicationType(GITHUB_RELEASE)
public class GithubReleaseUpdateChecker extends UpdateChecker {

  private static final Supplier<NewVersionNotFoundException> exceptionSupplier = () ->
    new NewVersionNotFoundException("Cannot find latest quit tag.");
  private static final TypeReference<List<Map<String, Object>>> TYPE_REF = new TypeReference<>() {};

  public GithubReleaseUpdateChecker(
    final String siteUrl,
    final String currentVersion
  ) {
    super(siteUrl, currentVersion);
  }

  @Override
  public void checkUpdate() throws IOException {
    final var url = new URL(getSiteUrl());
    final var path = url.getPath().split("/");
    final var lastPathElement = path[path.length - 1];
    final var isLatest = lastPathElement.equals("latest");
    final var isPrerelease = lastPathElement.equals("prerelease");
    var version = "";

    try {
      final var releases = UpdateCheckerUtils.readValue(
        new URL(
          url.getProtocol(),
          "api.github.com",
          "/repos/%s/%s/releases".formatted(path[1], path[2])
        ),
        TYPE_REF
      );

      version =
        (
          (String) (
            isPrerelease
              ? releases.stream()
              : releases
                .stream()
                .filter(release -> !((boolean) release.get("prerelease")))
          ).findFirst()
            .orElseThrow(exceptionSupplier)
            .get("tag_name")
        );
    } catch (IOException e) {
      // ignored
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    Optional<Element> versionElement = Optional.empty();

    if (version.isEmpty()) {
      versionElement =
        Optional.ofNullable(
          getJsoupConnectionInstance(
            new URL(
              url.getProtocol(),
              "github.com",
              "/%s/%s/releases%s".formatted(
                  path[1],
                  path[2],
                  isLatest ? "/latest" : ""
                )
            )
              .toString()
          )
            .get()
            .selectFirst(
              isLatest
                ? "span.css-truncate-target span"
                : "div.css-truncate-target span"
            )
        );
    }

    setNewVersion(
      (
        version.isEmpty()
          ? versionElement.orElseThrow(exceptionSupplier).text()
          : version
      ).replaceFirst("^v", "")
    );
  }
}
