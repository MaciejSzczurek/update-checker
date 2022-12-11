package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.WINAPP2;

import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import java.io.IOException;

@ApplicationType(WINAPP2)
public class Winapp2UpdateChecker extends UpdateChecker {

  public Winapp2UpdateChecker(
    final String siteUrl,
    final String currentVersion
  ) {
    super(siteUrl, currentVersion);
  }

  @Override
  public void checkUpdate() throws IOException {
    final String body = getJsoupConnectionInstance()
      .get()
      .select("body")
      .html();

    setNewVersion(
      body.substring(body.indexOf("; Version: ") + 11, body.indexOf(" ;"))
    );
  }
}
