package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.PROMODS;

import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import com.maciejszczurek.updatechecker.chrome.service.ChromeDriverHolder;
import lombok.Setter;
import org.openqa.selenium.By;

@ApplicationType(PROMODS)
public class PromodsUpdateChecker extends UpdateChecker {

  @Setter
  private ChromeDriverHolder chromeDriverHolder;

  public PromodsUpdateChecker(
    final String siteUrl,
    final String currentVersion
  ) {
    super(siteUrl, currentVersion);
  }

  @Override
  public void checkUpdate() {
    chromeDriverHolder.run(chromeDriver -> {
      chromeDriver.get(getSiteUrl());

      setNewVersion(
        chromeDriver
          .findElement(
            By.cssSelector(
              "#compat > tbody > tr:nth-child(1) > td:nth-child(3) > center > font"
            )
          )
          .getText()
          .substring(1)
      );
    });
  }
}
