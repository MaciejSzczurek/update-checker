package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.NODEJS;

import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import java.io.IOException;

@ApplicationType(NODEJS)
public class NodejsUpdateChecker extends UpdateChecker {

  public NodejsUpdateChecker(
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
        .select(
          "#main > div > article > section:nth-child(2) > p.color-lightgray > strong"
        )
        .html()
    );
  }
}
