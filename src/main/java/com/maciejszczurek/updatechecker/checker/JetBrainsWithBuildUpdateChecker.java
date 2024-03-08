package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.JETBRAINS_WITH_BUILD;

import com.fasterxml.jackson.databind.JsonNode;
import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import com.maciejszczurek.updatechecker.util.UpdateCheckerUtils;
import com.maciejszczurek.updatechecker.util.UrlBuilder;
import java.io.IOException;

@ApplicationType(JETBRAINS_WITH_BUILD)
public class JetBrainsWithBuildUpdateChecker extends UpdateChecker {

  public JetBrainsWithBuildUpdateChecker(
    final String siteUrl,
    final String currentVersion
  ) {
    super(siteUrl, currentVersion);
  }

  @Override
  public void checkUpdate() throws IOException, InterruptedException {
    String productCode = getSiteUrl()
      .replace(
        "https://data.services.jetbrains.com/products/releases?code=",
        ""
      );
    final int indexOfPercent = productCode.indexOf('%');
    if (indexOfPercent == -1) {
      productCode = productCode.substring(0, productCode.indexOf('&'));
    } else {
      productCode = productCode.substring(0, indexOfPercent);
    }

    final JsonNode node = UpdateCheckerUtils
      .readTree(UrlBuilder.build(getSiteUrl()))
      .findValue(productCode);

    setNewVersion(
      "%s.%s".formatted(
          node.findValue("version").asText(),
          node.findValue("build").asText()
        )
    );
  }
}
