package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.ZSH;

import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import java.io.IOException;

@ApplicationType(ZSH)
public class ZshUpdateChecker extends UpdateChecker {

  public ZshUpdateChecker(final String siteUrl, final String currentVersion) {
    super(siteUrl, currentVersion);
  }

  @Override
  public void checkUpdate() throws IOException {
    setNewVersion(
      getJsoupConnectionInstance()
        .get()
        .selectFirst("body > p:nth-child(5) > a:nth-child(1)")
        .text()
        .replace("zsh-", "")
        .replace(".tar.xz", "")
    );
  }
}
