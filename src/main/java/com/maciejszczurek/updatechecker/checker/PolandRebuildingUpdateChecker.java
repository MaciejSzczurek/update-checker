package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.POLAND_REBUILDING;

import com.maciejszczurek.updatechecker.application.NewVersionNotFoundException;
import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import java.io.IOException;

@ApplicationType(POLAND_REBUILDING)
public class PolandRebuildingUpdateChecker extends UpdateChecker {

  public PolandRebuildingUpdateChecker(
    final String siteUrl,
    final String currentVersion
  ) {
    super(siteUrl, currentVersion);
  }

  @Override
  public void checkUpdate() throws IOException {
    setNewVersion(
      getJsoupConnectionInstance()
        .get()
        .select("#dl-map > div.elements-container.flex > div.download-threads")
        .stream()
        .filter(element ->
          !element
            .select("div > p:nth-child(5) > a")
            .hasClass("button-inactive")
        )
        .findFirst()
        .orElseThrow(() ->
          new NewVersionNotFoundException("Unable to find a new version.")
        )
        .select("div > p:nth-child(2) > strong")
        .text()
        .replace("Poland Rebuilding ", "")
    );
  }
}
