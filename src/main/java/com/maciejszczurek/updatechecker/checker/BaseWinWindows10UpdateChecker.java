package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.BASEWIN_WINDOWS10;

import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import java.io.IOException;

@ApplicationType(BASEWIN_WINDOWS10)
public class BaseWinWindows10UpdateChecker extends UpdateChecker {

  public BaseWinWindows10UpdateChecker(
    final String siteUrl,
    final String currentVersion
  ) {
    super(siteUrl, currentVersion);
  }

  @Override
  public void checkUpdate() throws IOException {
    final String text = getJsoupConnectionInstance()
      .get()
      .select("p.MsoNormal:nth-child(4)")
      .text()
      .replace("Windows 11 ", "");

    setNewVersion(text.substring(0, text.indexOf(" (")));
  }
}
