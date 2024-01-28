package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.GITEA_FILE;

import com.fasterxml.jackson.core.type.TypeReference;
import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import com.maciejszczurek.updatechecker.util.UpdateCheckerUtils;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@ApplicationType(GITEA_FILE)
public class GiteaFileUpdateChecker extends UpdateChecker {

  private static final TypeReference<List<Map<String, Object>>> TYPE_REF =
    new TypeReference<>() {};

  public GiteaFileUpdateChecker(String siteUrl, String currentVersion) {
    super(siteUrl, currentVersion);
  }

  @Override
  public void checkUpdate() throws IOException, InterruptedException {
    var uri = URI.create(getSiteUrl());
    var splitPath = Arrays.asList(uri.getPath().split("/"));
    var giteaPath = String.join(
      "/",
      splitPath.subList(6, splitPath.size() - 1)
    );
    setNewVersion(
      UpdateCheckerUtils
        .readValue(
          URI
            .create(
              "%s://%s/api/v1/repos/%s/%s/contents/%s".formatted(
                  uri.getScheme(),
                  uri.getHost(),
                  splitPath.get(1),
                  splitPath.get(2),
                  URLEncoder.encode(
                    "%s%s".formatted(
                        giteaPath,
                        !giteaPath.isEmpty() ? "/." : ""
                      ),
                    StandardCharsets.UTF_8
                  )
                )
            )
            .toURL(),
          TYPE_REF
        )
        .stream()
        .filter(content -> content.get("html_url").equals(getSiteUrl()))
        .findFirst()
        .map(content -> ((String) content.get("sha")).substring(0, 10))
        .orElseThrow()
    );
  }
}
