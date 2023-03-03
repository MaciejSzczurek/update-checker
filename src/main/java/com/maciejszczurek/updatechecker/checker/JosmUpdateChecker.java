package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.JOSM;

import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import com.maciejszczurek.updatechecker.chrome.service.ChromeDriverHolder;
import java.io.IOException;
import java.util.Objects;
import lombok.Setter;
import org.openqa.selenium.By;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;

@ApplicationType(JOSM)
public class JosmUpdateChecker extends UpdateChecker {

  @Setter(onMethod_ = @Autowired)
  private ProxyFactoryBean chromeDriverHolder;

  public JosmUpdateChecker(final String siteUrl, final String currentVersion) {
    super(siteUrl, currentVersion);
  }

  @Override
  public void checkUpdate() throws IOException {
    Objects
      .requireNonNull(((ChromeDriverHolder) chromeDriverHolder.getObject()))
      .run(chromeDriver -> {
        chromeDriver.get(getSiteUrl());
        final String text = chromeDriver
          .findElement(
            By.cssSelector(
              "#wikipage > table > tbody > tr:nth-child(2) > td > p"
            )
          )
          .getText();

        setNewVersion(
          text.substring(
            text.indexOf("(najnowsza wersja stabilna ") + 27,
            text.indexOf(')')
          )
        );
      });
  }
}
