package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.SPRING_BOOT;

import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import com.maciejszczurek.updatechecker.http.HttpBuilderFactory;
import com.maciejszczurek.updatechecker.util.UpdateCheckerUtils;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@ApplicationType(SPRING_BOOT)
public class SpringBootUpdateChecker extends UpdateChecker {

  public SpringBootUpdateChecker(
    final String siteUrl,
    final String currentVersion
  ) {
    super(siteUrl, currentVersion);
  }

  @Override
  public void checkUpdate() throws IOException {
    try {
      final var siteUri = new URI(getSiteUrl());

      setNewVersion(
        UpdateCheckerUtils
          .readTree(
            HttpBuilderFactory
              .getBuilder()
              .build()
              .send(
                HttpRequest
                  .newBuilder()
                  .header("Accept", "application/vnd.initializr.v2.2+json")
                  .uri(
                    new URI(
                      siteUri.getScheme(),
                      siteUri.getHost(),
                      "/metadata/client",
                      siteUri.getFragment()
                    )
                  )
                  .build(),
                HttpResponse.BodyHandlers.ofInputStream()
              )
              .body()
          )
          .get("bootVersion")
          .get("default")
          .asText()
      );
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException(e);
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }
}
