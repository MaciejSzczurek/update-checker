package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.PHP;

import com.maciejszczurek.updatechecker.application.NewVersionNotFoundException;
import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import java.io.IOException;
import java.util.Optional;

@ApplicationType(PHP)
public class PhpUpdateChecker extends UpdateChecker {

  public PhpUpdateChecker(final String siteUrl, final String currentVersion) {
    super(siteUrl, currentVersion);
  }

  @Override
  public void checkUpdate() throws IOException {
    setNewVersion(
      Optional
        .ofNullable(
          getJsoupConnectionInstance().get().selectFirst("#layout-content h3")
        )
        .map(element -> element.attr("id").substring(1))
        .orElseThrow(() ->
          new NewVersionNotFoundException(
            "Cannot find header with latest PHP version."
          )
        )
    );
  }
}
