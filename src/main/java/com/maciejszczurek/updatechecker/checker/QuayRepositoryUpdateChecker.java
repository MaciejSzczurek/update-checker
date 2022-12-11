package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.QUAY_REPOSITORY;

import com.fasterxml.jackson.core.type.TypeReference;
import com.maciejszczurek.updatechecker.application.NewVersionNotFoundException;
import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import com.maciejszczurek.updatechecker.checker.util.DockerHubUtils;
import com.maciejszczurek.updatechecker.http.HttpBuilderFactory;
import com.maciejszczurek.updatechecker.util.UpdateCheckerUtils;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@ApplicationType(QUAY_REPOSITORY)
public class QuayRepositoryUpdateChecker extends UpdateChecker {

  private static final TypeReference<Map<String, Object>> TYPE_REF = new TypeReference<>() {};

  public QuayRepositoryUpdateChecker(
    final String siteUrl,
    final String currentVersion
  ) {
    super(siteUrl, currentVersion);
  }

  @Override
  public void checkUpdate() throws IOException, InterruptedException {
    final String tag;
    final var uri = URI.create(getSiteUrl());
    final var pathSegments = uri.getPath().split("/");

    if (pathSegments.length < 4) {
      throw new NewVersionNotFoundException(
        "Path of %s is to short and path does not have the right number of segments.".formatted(
            uri
          )
      );
    }

    try {
      tag = DockerHubUtils.extractTagFromQuery(uri, "tag");
    } catch (NoSuchElementException e) {
      throw new NewVersionNotFoundException("Tag not found.");
    }

    final var response = HttpBuilderFactory
      .getBuilder()
      .build()
      .send(
        HttpRequest
          .newBuilder()
          .uri(
            URI.create(
              "https://quay.io/api/v1/repository/%s/%s/tag/?tag=%s&onlyActiveTags=true".formatted(
                  pathSegments[2],
                  pathSegments[3],
                  tag
                )
            )
          )
          .build(),
        HttpResponse.BodyHandlers.ofInputStream()
      );

    final var tags = (List<?>) (
      (Map<?, ?>) UpdateCheckerUtils
        .getObjectMapper()
        .readValue(response.body(), TYPE_REF)
    ).get("tags");

    final var manifestDigest =
      (
        (String) (
          (Map<?, ?>) tags
            .stream()
            .filter(tag2 -> ((Map<?, ?>) tag2).get("name").equals(tag))
            .findFirst()
            .orElseThrow(() -> new NewVersionNotFoundException("Tag not found.")
            )
        ).get("manifest_digest")
      );

    final var version = tag.equals("latest")
      ? tags
        .stream()
        .map(tag2 -> ((Map<?, ?>) tag2))
        .filter(tag2 -> tag2.get("manifest_digest").equals(manifestDigest))
        .filter(tag2 -> !tag2.get("name").equals(tag))
        .findFirst()
        .map(tag2 -> "%s-".formatted(tag2.get("name")))
        .orElse("")
      : "";

    setNewVersion(
      "%s%s".formatted(
          version,
          manifestDigest.replace("sha256:", "").substring(0, 12)
        )
    );
  }
}
