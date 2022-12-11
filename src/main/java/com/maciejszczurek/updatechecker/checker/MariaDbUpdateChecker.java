package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.MARIA_DB;

import com.fasterxml.jackson.core.type.TypeReference;
import com.maciejszczurek.updatechecker.application.NewVersionNotFoundException;
import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import com.maciejszczurek.updatechecker.util.UpdateCheckerUtils;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

@ApplicationType(MARIA_DB)
public class MariaDbUpdateChecker extends UpdateChecker {

  private static final TypeReference<Map<String, List<Map<String, Object>>>> TYPE_REFERENCE = new TypeReference<>() {};

  public MariaDbUpdateChecker(
    final String siteUrl,
    final String currentVersion
  ) {
    super(siteUrl, currentVersion);
  }

  @Override
  public void checkUpdate() throws IOException, InterruptedException {
    setNewVersion(
      (String) UpdateCheckerUtils
        .readValue(
          new URL(
            "https://downloads.mariadb.org/rest-api/mariadb/all-releases/?olderReleases=false"
          ),
          TYPE_REFERENCE
        )
        .get("releases")
        .stream()
        .filter(release -> release.get("status").equals("stable"))
        .findFirst()
        .orElseThrow(() ->
          new NewVersionNotFoundException(
            "A stable version of MariaDB was not found."
          )
        )
        .get("release_number")
    );
  }
}
