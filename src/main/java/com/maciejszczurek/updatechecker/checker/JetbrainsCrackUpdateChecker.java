package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.JETBRAINS_CRACK;

import com.maciejszczurek.updatechecker.application.NewVersionNotFoundException;
import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import java.io.IOException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ApplicationType(JETBRAINS_CRACK)
public class JetbrainsCrackUpdateChecker extends UpdateChecker {

  private final Pattern versionPattern = Pattern.compile("\\((\\d+)\\)");

  public JetbrainsCrackUpdateChecker(
    final String siteUrl,
    final String currentVersion
  ) {
    super(siteUrl, currentVersion);
  }

  @Override
  public void checkUpdate() throws IOException {
    setNewVersion(
      Optional.ofNullable(
        getJsoupConnectionInstance().get().selectFirst("body > header > p")
      )
        .map(element -> versionPattern.matcher(element.text()))
        .filter(Matcher::find)
        .orElseThrow(() ->
          new NewVersionNotFoundException("Cannot find header paragraph")
        )
        .group(1)
    );
  }
}
