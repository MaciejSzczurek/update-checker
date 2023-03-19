package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.NSANE_DOWN;

import com.maciejszczurek.updatechecker.application.NewVersionNotFoundException;
import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import com.maciejszczurek.updatechecker.cookie.service.CookieHolder;
import com.maciejszczurek.updatechecker.http.HttpBuilderFactory;
import java.io.IOException;
import java.net.CookieHandler;
import java.net.http.HttpResponse;
import java.util.Optional;
import lombok.Setter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.springframework.transaction.annotation.Transactional;

@ApplicationType(NSANE_DOWN)
public class NsaneDownUpdateChecker
  extends UpdateChecker
  implements NameSetter {

  @Setter
  private String name;

  @Setter
  private CookieHolder cookieHolder;

  @Setter
  private CookieHandler cookieHandler;

  public NsaneDownUpdateChecker(
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

    if (response.statusCode() == 503) {
      cookieHolder.regenerateCookies("https://nsaneforums.com");

      response =
        httpClient.send(
          HttpBuilderFactory.buildRequest(getSiteUrl()),
          HttpResponse.BodyHandlers.ofString()
        );
    }

    setNewVersion(
      Optional
        .ofNullable(
          Jsoup
            .parse(response.body())
            .selectFirst("#ipsLayout_mainArea span.ipsContained")
        )
        .map(Element::text)
        .orElseThrow(() -> new NewVersionNotFoundException("Selector is empty.")
        )
        .replace(name + " ", "")
    );
  }
}
