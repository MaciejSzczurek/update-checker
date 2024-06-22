package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.ULTRA_VNC;

import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import java.io.IOException;

@ApplicationType(ULTRA_VNC)
public class UltraVNCUpdateChecker extends UpdateChecker {

  public UltraVNCUpdateChecker(String siteUrl, String currentVersion) {
    super(siteUrl, currentVersion);
  }

  @Override
  public void checkUpdate() throws IOException, InterruptedException {
    setNewVersion(
      getJsoupConnectionInstance()
        .get()
        .select(
          "#sp-component > div > div.category-list > div > div > div > p:nth-child(1) > span"
        )
        .text()
        .replace("Latest release version: ", "")
    );
  }
}
