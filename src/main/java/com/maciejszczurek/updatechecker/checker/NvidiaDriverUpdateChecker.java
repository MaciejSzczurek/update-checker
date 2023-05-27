package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.NVIDIA_DRIVER;

import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import java.io.IOException;

@ApplicationType(NVIDIA_DRIVER)
public class NvidiaDriverUpdateChecker extends UpdateChecker {

  public NvidiaDriverUpdateChecker(
    final String siteUrl,
    final String currentVersion
  ) {
    super(siteUrl, currentVersion);
  }

  @Override
  public void checkUpdate() throws IOException {
    setNewVersion(
      getJsoupConnectionInstance(
        "https:%s".formatted(getJsoupConnectionInstance().get().text())
      )
        .get()
        .select("#tdVersion")
        .text()
        .replace(" WHQL", "")
    );
  }
}
