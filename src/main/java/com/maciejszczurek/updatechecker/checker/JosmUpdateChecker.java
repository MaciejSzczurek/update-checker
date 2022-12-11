package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.JOSM;

import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import java.io.IOException;

@ApplicationType(JOSM)
public class JosmUpdateChecker extends UpdateChecker {

  public JosmUpdateChecker(final String siteUrl, final String currentVersion) {
    super(siteUrl, currentVersion);
  }

  @Override
  public void checkUpdate() throws IOException {
    final String text = getJsoupConnectionInstance()
      .get()
      .select("#wikipage > table > tbody > tr:nth-child(2) > td > p")
      .text();

    setNewVersion(
      text.substring(
        text.indexOf("(najnowsza wersja stabilna ") + 27,
        text.indexOf(')')
      )
    );
  }
}
