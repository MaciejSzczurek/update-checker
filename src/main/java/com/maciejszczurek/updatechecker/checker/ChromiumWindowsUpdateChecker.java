package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.CHROMIUM_WINDOWS;

import com.maciejszczurek.updatechecker.application.NewVersionNotFoundException;
import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import com.maciejszczurek.updatechecker.util.UpdateCheckerUtils;
import java.io.IOException;
import java.net.URL;
import java.util.stream.Stream;

@ApplicationType(CHROMIUM_WINDOWS)
public class ChromiumWindowsUpdateChecker extends UpdateChecker {

  public ChromiumWindowsUpdateChecker(
    final String siteUrl,
    final String currentVersion
  ) {
    super(siteUrl, currentVersion);
  }

  @Override
  public void checkUpdate() throws IOException, InterruptedException {
    setNewVersion(
      UpdateCheckerUtils
        .readTree(new URL(getSiteUrl()))
        .get("chromium")
        .get(
          Stream
            .of(new URL(getSiteUrl()).getQuery().split("&"))
            .map(param -> param.split("="))
            .filter(param -> param.length == 2)
            .filter(param -> param[0].equals("os"))
            .findFirst()
            .orElseThrow(() ->
              new NewVersionNotFoundException("Cannot find Chromium version")
            )[1]
        )
        .get("version")
        .textValue()
    );
  }
}
