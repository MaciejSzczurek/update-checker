package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.JETBRAINS_CRACK;

import com.maciejszczurek.updatechecker.application.NewVersionNotFoundException;
import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import java.io.IOException;
import java.util.Optional;

@ApplicationType(JETBRAINS_CRACK)
public class JetbrainsCrackUpdateChecker extends UpdateChecker {

  private static final String LINK_QUERY = "?v=";
  private static final int LINK_QUERY_LENGTH = LINK_QUERY.length();

  public JetbrainsCrackUpdateChecker(
    final String siteUrl,
    final String currentVersion
  ) {
    super(siteUrl, currentVersion);
  }

  @Override
  public void checkUpdate() throws IOException {
    final var hrefLink = Optional
      .ofNullable(
        getJsoupConnectionInstance()
          .get()
          .selectFirst("head > link[href^='styles']")
      )
      .orElseThrow(() ->
        new NewVersionNotFoundException("Cannot find styles link")
      )
      .attr("href");

    setNewVersion(
      hrefLink.substring(hrefLink.lastIndexOf(LINK_QUERY) + LINK_QUERY_LENGTH)
    );
  }
}
