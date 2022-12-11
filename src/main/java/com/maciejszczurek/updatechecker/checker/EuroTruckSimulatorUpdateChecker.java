package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.EURO_TRUCK_SIMULATOR;

import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import com.maciejszczurek.updatechecker.cookie.service.CookieHolder;
import com.maciejszczurek.updatechecker.http.HttpBuilderFactory;
import java.io.IOException;
import java.net.CookieHandler;
import java.net.http.HttpResponse;
import lombok.Setter;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@ApplicationType(EURO_TRUCK_SIMULATOR)
public class EuroTruckSimulatorUpdateChecker extends UpdateChecker {

  @Setter(onMethod_ = @Autowired)
  private CookieHolder cookieHolder;

  @Setter(onMethod_ = @Autowired)
  private CookieHandler cookieHandler;

  public EuroTruckSimulatorUpdateChecker(
    final String siteUrl,
    final String currentVersion
  ) {
    super(siteUrl, currentVersion);
  }

  @Override
  @Transactional
  public void checkUpdate() throws IOException, InterruptedException {
    final var httpClient = HttpBuilderFactory
      .getBuilder()
      .cookieHandler(cookieHandler)
      .build();
    var response = httpClient.send(
      HttpBuilderFactory.buildRequest(getSiteUrl()),
      HttpResponse.BodyHandlers.ofString()
    );

    if (response.statusCode() == 403) {
      cookieHolder.regenerateCookies(getSiteUrl());

      response =
        httpClient.send(
          HttpBuilderFactory.buildRequest(getSiteUrl()),
          HttpResponse.BodyHandlers.ofString()
        );
    }

    setNewVersion(
      Jsoup
        .parse(response.body())
        .select("div.game-info > h3:nth-child(3)")
        .text()
        .replace("Game: V ", "")
    );
  }
}
