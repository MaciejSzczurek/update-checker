package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.TC_UP;

import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import java.io.IOException;

@ApplicationType(TC_UP)
public class TcUpUpdateChecker extends UpdateChecker {

  public TcUpUpdateChecker(final String siteUrl, final String currentVersion) {
    super(siteUrl, currentVersion);
  }

  @Override
  public void checkUpdate() throws IOException {
    setNewVersion(
      getJsoupConnectionInstance()
        .get()
        .select(
          "#inner_content-33-11 > p:nth-child(1) > mark.has-inline-color.has-luminous-vivid-amber-color"
        )
        .text()
    );
  }
}
