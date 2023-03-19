package com.maciejszczurek.updatechecker.cookie.service;

import com.maciejszczurek.updatechecker.chrome.service.ChromeDriverHolder;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.URI;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@Service
public class CookieHolder {

  private final ChromeDriverHolder chromeDriverHolder;

  private final CookieManager cookieManager;
  private final TransactionTemplate transactionTemplate;

  public CookieHolder(
    final ChromeDriverHolder chromeDriverHolder,
    final CookieManager cookieManager,
    final PlatformTransactionManager transactionManager
  ) {
    this.chromeDriverHolder = chromeDriverHolder;
    this.cookieManager = cookieManager;
    transactionTemplate = new TransactionTemplate(transactionManager);
  }

  public void regenerateCookies(final String uri) {
    chromeDriverHolder.run(driver ->
      transactionTemplate.executeWithoutResult(status -> {
        driver.get(uri);

        new WebDriverWait(driver, Duration.ofMinutes(1))
          .until(driver1 -> driver1.findElements(By.id("cf-content")).isEmpty()
          );

        driver
          .manage()
          .getCookies()
          .stream()
          .map(cookie -> {
            final var httpCookie = new HttpCookie(
              cookie.getName(),
              cookie.getValue()
            );

            httpCookie.setDomain(cookie.getDomain());
            httpCookie.setPath(cookie.getPath());
            if (cookie.getExpiry() != null) {
              httpCookie.setMaxAge(
                ZonedDateTime
                  .now()
                  .until(
                    cookie
                      .getExpiry()
                      .toInstant()
                      .atZone(ZoneId.systemDefault()),
                    ChronoUnit.SECONDS
                  )
              );
            }
            httpCookie.setSecure(cookie.isSecure());
            httpCookie.setHttpOnly(cookie.isHttpOnly());

            return httpCookie;
          })
          .forEach(cookie ->
            cookieManager.getCookieStore().add(URI.create(uri), cookie)
          );
      })
    );
  }
}
