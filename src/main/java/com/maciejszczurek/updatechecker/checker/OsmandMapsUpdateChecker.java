package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.OSMAND_MAPS;

import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import java.io.IOException;

@ApplicationType(OSMAND_MAPS)
public class OsmandMapsUpdateChecker extends UpdateChecker {

  public OsmandMapsUpdateChecker(
    final String siteUrl,
    final String currentVersion
  ) {
    super(siteUrl, currentVersion);
  }

  @Override
  public void checkUpdate() throws IOException {
    getJsoupConnectionInstance()
      .get()
      .select("body > div > table > tbody > tr")
      .stream()
      .filter(element ->
        element
          .select("td:nth-child(1) > a")
          .html()
          .equals("Poland_lower-silesian_europe_2.obf.zip")
      )
      .findFirst()
      .ifPresentOrElse(
        element -> setNewVersion(element.select("td:nth-child(2)").html()),
        () -> setNewVersion("")
      );
  }
}
