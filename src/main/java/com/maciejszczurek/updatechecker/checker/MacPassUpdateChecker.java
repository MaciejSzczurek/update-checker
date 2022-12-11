package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.MAC_PASS;

import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import java.io.IOException;

@ApplicationType(MAC_PASS)
public class MacPassUpdateChecker extends UpdateChecker {

  public MacPassUpdateChecker(
    final String siteUrl,
    final String currentVersion
  ) {
    super(siteUrl, currentVersion);
  }

  @Override
  public void checkUpdate() throws IOException {
    final String text = getJsoupConnectionInstance()
      .get()
      .select("body > div.content > div.buttons > div")
      .html();

    setNewVersion(
      text.substring(text.indexOf("Version ") + 8, text.indexOf(" for macOS"))
    );
  }
}
