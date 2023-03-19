package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.PYTHON_WINDOWS_LIBS;

import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import com.maciejszczurek.updatechecker.chrome.service.ChromeDriverHolder;
import lombok.Setter;
import org.openqa.selenium.By;

@ApplicationType(PYTHON_WINDOWS_LIBS)
public class PythonWindowLibsUpdateChecker extends UpdateChecker {

  @Setter
  private ChromeDriverHolder chromeDriverHolder;

  public PythonWindowLibsUpdateChecker(
    final String siteUrl,
    final String currentVersion
  ) {
    super(siteUrl, currentVersion);
  }

  @Override
  public void checkUpdate() {
    chromeDriverHolder.run(chromeDriver -> {
      chromeDriver.get(getSiteUrl());

      final var filename = chromeDriver
        .findElement(
          By.id(getSiteUrl().substring(getSiteUrl().indexOf("#") + 1))
        )
        .findElement(By.xpath(".//../ul/li[1]/a"))
        .getText();
      final var firstBreakingHyphen = filename.indexOf('‑') + 1;

      setNewVersion(
        filename.substring(
          firstBreakingHyphen,
          filename.indexOf('‑', firstBreakingHyphen)
        )
      );
    });
  }
}
