package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.JFROG_METADATA;

import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import com.maciejszczurek.updatechecker.checker.util.MavenVersionReaderUtility;
import com.maciejszczurek.updatechecker.util.UrlBuilder;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@ApplicationType(JFROG_METADATA)
public class JfrogMetadataUpdateChecker extends UpdateChecker {

  public JfrogMetadataUpdateChecker(
    final String siteUrl,
    final String currentVersion
  ) {
    super(siteUrl, currentVersion);
  }

  @SuppressWarnings("CssInvalidHtmlTagReference")
  @Override
  public void checkUpdate() throws IOException {
    final var url = UrlBuilder.build(getSiteUrl());
    final var path = url.getPath().replace("/ui/native/", "");
    final var indexOfSlash = path.indexOf('/');

    setNewVersion(
      MavenVersionReaderUtility.readVersion(
        getJsoupConnectionInstance(
          UrlBuilder.build(
            url.getProtocol(),
            url.getHost(),
            url.getPort(),
            "/ui/api/v1/download?repoKey=%s&path=%s".formatted(
                path.substring(0, indexOfSlash),
                URLEncoder.encode(
                  "%s/maven-metadata.xml".formatted(
                      path.substring(indexOfSlash + 1)
                    ),
                  StandardCharsets.UTF_8
                )
              )
          )
            .toString()
        )
          .get()
      )
    );
  }
}
