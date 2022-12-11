package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.WORDPRESS_PHP;

import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import java.io.IOException;

@ApplicationType(WORDPRESS_PHP)
public class WordpressPhpUpdateChecker extends UpdateChecker {

  private static final String PHP_VERSION_CONSTANT = "defaultPhpVersion='php";
  private static final int PHP_VERSION_CONSTANT_LENGTH = PHP_VERSION_CONSTANT.length();

  public WordpressPhpUpdateChecker(
    final String siteUrl,
    final String currentVersion
  ) {
    super(siteUrl, currentVersion);
  }

  @Override
  public void checkUpdate() throws IOException {
    var body = getJsoupConnectionInstance().get().html();
    body =
      body.substring(
        body.indexOf(PHP_VERSION_CONSTANT) + PHP_VERSION_CONSTANT_LENGTH
      );

    setNewVersion(body.substring(0, body.indexOf('\'')));
  }
}
