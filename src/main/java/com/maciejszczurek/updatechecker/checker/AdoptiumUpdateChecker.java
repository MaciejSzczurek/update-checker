package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.ADOPTIUM;

import com.fasterxml.jackson.core.type.TypeReference;
import com.maciejszczurek.updatechecker.application.NewVersionNotFoundException;
import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import com.maciejszczurek.updatechecker.util.UpdateCheckerUtils;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import lombok.Setter;

@ApplicationType(ADOPTIUM)
public class AdoptiumUpdateChecker extends UpdateChecker implements NameSetter {

  private static final TypeReference<Map<String, List<Map<String, Object>>>> TYPE_REFERENCE = new TypeReference<>() {};

  @Setter
  private String name;

  public AdoptiumUpdateChecker(
    final String siteUrl,
    final String currentVersion
  ) {
    super(siteUrl, currentVersion);
  }

  @Override
  public void checkUpdate() throws IOException, InterruptedException {
    setNewVersion(
      (String) (
        UpdateCheckerUtils.readValue(
          URI
            .create(
              "https://api.adoptium.net/v3/info/release_versions?architecture=x64&page=0&page_size=10&release_type=ga&sort_order=DESC&os=%s".formatted(
                  name.toLowerCase().endsWith("windows") ? "windows" : "linux"
                )
            )
            .toURL(),
          TYPE_REFERENCE
        )
      ).get("versions")
        .parallelStream()
        .filter(map -> !map.containsKey("pre"))
        .findFirst()
        .orElseThrow(() ->
          new NewVersionNotFoundException("Cannot find Adoptium version.")
        )
        .get("openjdk_version")
    );
  }
}
