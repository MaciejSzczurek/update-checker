package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.GPG4WIN;

import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import java.io.IOException;

@ApplicationType(GPG4WIN)
public class Gpg4winUpdateChecker extends UpdateChecker {

  public Gpg4winUpdateChecker(
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
        .select("body > div.wrapper > div.content > meta")
        .attr("content")
        .replace("0; url=https://files.gpg4win.org/gpg4win-", "")
        .replace(".exe", "")
    );
  }
}
