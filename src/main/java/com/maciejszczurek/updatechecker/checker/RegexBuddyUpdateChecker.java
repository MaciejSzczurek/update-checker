package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.REGEX_BUDDY;

import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import java.io.IOException;

@ApplicationType(REGEX_BUDDY)
public class RegexBuddyUpdateChecker extends UpdateChecker {

  public RegexBuddyUpdateChecker(
    final String siteUrl,
    final String currentVersion
  ) {
    super(siteUrl, currentVersion);
  }

  @Override
  public void checkUpdate() throws IOException {
    final var text = getJsoupConnectionInstance()
      .get()
      .select("#body > h2:nth-child(7)")
      .html()
      .replace("RegexBuddy ", "");

    setNewVersion(text.substring(0, text.indexOf("\u2002–\u2002")));
  }
}
