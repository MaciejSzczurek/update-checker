package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.NOTEPAD_PP;

import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import java.io.IOException;

@ApplicationType(NOTEPAD_PP)
public class NotepadPPUpdateChecker extends UpdateChecker {

  public NotepadPPUpdateChecker(
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
        .select("body > div > header > div > div > p > a > strong")
        .html()
        .replace("Current Version ", "")
    );
  }
}
