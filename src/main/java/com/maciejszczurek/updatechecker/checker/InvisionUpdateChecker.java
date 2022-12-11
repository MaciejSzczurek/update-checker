package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.INVISION;

import com.maciejszczurek.updatechecker.application.NewVersionNotFoundException;
import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import java.io.IOException;

@ApplicationType(INVISION)
public class InvisionUpdateChecker extends UpdateChecker {

  public InvisionUpdateChecker(
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
        .select(".topics li")
        .stream()
        .map(el -> el.selectFirst("a").text())
        .filter(el -> el.endsWith(" Release!"))
        .findFirst()
        .orElseThrow(() ->
          new NewVersionNotFoundException(
            "Cannot find new version of Invision."
          )
        )
        .replace("Invision ", "")
        .replace(" Release!", "")
    );
  }
}
