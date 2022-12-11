package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.PROMODS;

import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import com.maciejszczurek.updatechecker.chrome.service.ChromeDriverHolder;
import java.util.Objects;
import lombok.Setter;
import org.openqa.selenium.By;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;

@ApplicationType(PROMODS)
public class PromodsUpdateChecker extends UpdateChecker {

  @Setter(onMethod_ = @Autowired)
  private ProxyFactoryBean chromeDriverFactory;

  public PromodsUpdateChecker(
    final String siteUrl,
    final String currentVersion
  ) {
    super(siteUrl, currentVersion);
  }

  @Override
  public void checkUpdate() {
    (
      (ChromeDriverHolder) Objects.requireNonNull(
        chromeDriverFactory.getObject()
      )
    ).run(chromeDriver -> {
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
