package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.NPM_REPO;

import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import com.maciejszczurek.updatechecker.util.UpdateCheckerUtils;
import com.maciejszczurek.updatechecker.util.UrlBuilder;
import java.io.IOException;

@ApplicationType(NPM_REPO)
public class NpmRepoUpdateChecker extends UpdateChecker {

  public NpmRepoUpdateChecker(
    final String siteUrl,
    final String currentVersion
  ) {
    super(siteUrl, currentVersion);
  }

  @Override
  public void checkUpdate() throws IOException, InterruptedException {
    final String[] tags = getSiteUrl()
      .replace("https://www.npmjs.com/package/", "")
      .split("/");

    setNewVersion(
      UpdateCheckerUtils
        .readTree(
          UrlBuilder.build(
            tags.length == 1 || tags.length == 3
              ? "https://registry.npmjs.org/%s".formatted(tags[0])
              : "https://registry.npmjs.org/%s/%s".formatted(tags[0], tags[1])
          )
        )
        .get("dist-tags")
        .get(
          tags.length >= 3 && tags[tags.length - 2].equals("v")
            ? tags[tags.length - 1]
            : "latest"
        )
        .asText()
    );
  }
}
