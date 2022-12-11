package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.DOCKER_HUB;

import com.fasterxml.jackson.core.type.TypeReference;
import com.maciejszczurek.updatechecker.application.NewVersionNotFoundException;
import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import com.maciejszczurek.updatechecker.checker.util.DockerHubUtils;
import com.maciejszczurek.updatechecker.util.UpdateCheckerUtils;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@ApplicationType(DOCKER_HUB)
public class DockerHubUpdateChecker extends UpdateChecker {

  private static final TypeReference<Map<String, Object>> TYPE_REF = new TypeReference<>() {};

  public DockerHubUpdateChecker(
    final String siteUrl,
    final String currentVersion
  ) {
    super(siteUrl, currentVersion);
  }

  @Override
  public void checkUpdate() throws IOException, InterruptedException {
    final var uri = URI.create(getSiteUrl());
    final var pathSegments = uri.getPath().split("/");
    final String tag;

    try {
      tag = DockerHubUtils.extractTagFromQuery(uri, "name");
    } catch (NoSuchElementException e) {
      throw new NewVersionNotFoundException("Tag not found.");
    }

    setNewVersion(
      Optional
        .ofNullable(
          (
            (String) (
              (Map<?, ?>) (
                (List<?>) UpdateCheckerUtils
                  .readValue(
                    URI
                      .create(
                        "https://hub.docker.com/v2/repositories/%s/tags/%s".formatted(
                            pathSegments[1].equals("_")
                              ? "library/%s".formatted(pathSegments[2])
                              : "%s/%s".formatted(
                                  pathSegments[2],
                                  pathSegments[3]
                                ),
                            tag
                          )
                      )
                      .toURL(),
                    TYPE_REF
                  )
                  .get("images")
              ).stream()
                .filter(image ->
                  ((Map<?, ?>) image).get("architecture").equals("amd64")
                )
                .findFirst()
                .orElseThrow(() ->
                  new NewVersionNotFoundException("Tag not found.")
                )
            ).get("digest")
          )
        )
        .orElseThrow(() -> new NewVersionNotFoundException("Digest is empty."))
        .replace("sha256:", "")
        .substring(0, 12)
    );
  }
}
