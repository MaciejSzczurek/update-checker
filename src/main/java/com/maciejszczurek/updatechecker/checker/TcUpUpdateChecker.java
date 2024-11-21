package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.TC_UP;

import com.maciejszczurek.updatechecker.application.NewVersionNotFoundException;
import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import java.io.IOException;
import java.util.Optional;

@ApplicationType(TC_UP)
public class TcUpUpdateChecker extends UpdateChecker {

  public TcUpUpdateChecker(final String siteUrl, final String currentVersion) {
    super(siteUrl, currentVersion);
  }

  @Override
  public void checkUpdate() throws IOException {
    setNewVersion(
      Optional
        .ofNullable(
          getJsoupConnectionInstance()
            .get()
            .selectFirst(
              "#inner_content-33-11 > p:nth-child(5) > span > span:nth-child(2)"
            )
        )
        .orElseThrow(() ->
          new NewVersionNotFoundException(
            "Not found selector with version of TC UP."
          )
        )
        .text()
    );
  }
}
