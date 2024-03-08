package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.NAVIGATION_MAPS;

import com.maciejszczurek.updatechecker.application.NewVersionNotFoundException;
import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import com.maciejszczurek.updatechecker.chrome.service.ChromeDriverHolder;
import com.maciejszczurek.updatechecker.http.HttpBuilderFactory;
import com.maciejszczurek.updatechecker.util.UrlBuilder;
import com.pivovarit.function.exception.WrappedException;
import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.Map;
import lombok.Setter;

@ApplicationType(NAVIGATION_MAPS)
public class NavigationMapsUpdateChecker extends UpdateChecker {

  private static final String DISCOVER_MEDIA_FILENAME = "DiscoverMedia2_EU-AS_";

  @Setter
  private ChromeDriverHolder chromeDriverHolder;

  public NavigationMapsUpdateChecker(
    final String siteUrl,
    final String currentVersion
  ) {
    super(siteUrl, currentVersion);
  }

  @Override
  public void checkUpdate() throws IOException, InterruptedException {
    final var siteUrl = UrlBuilder.build(getSiteUrl());
    final var document = getJsoupConnectionInstance().get();

    final var webpackRuntimeUrl = document
      .select("link[as='script']")
      .stream()
      .map(element -> element.attr("href"))
      .filter(element -> element.contains("webpack-runtime"))
      .findFirst()
      .orElseThrow(() ->
        new NewVersionNotFoundException("Cannot find webpack-runtime file.")
      );
    final var webpackRuntime = HttpBuilderFactory
      .getBuilder()
      .build()
      .send(
        HttpBuilderFactory.buildRequest(
          UrlBuilder.build(siteUrl.getProtocol(), siteUrl.getHost(), webpackRuntimeUrl)
            .toString()
        ),
        HttpResponse.BodyHandlers.ofString()
      )
      .body();
    final var link = webpackRuntimeUrl.substring(
      0,
      webpackRuntimeUrl.lastIndexOf("/")
    );
    chromeDriverHolder.run(chromeDriver -> {
      final var prefixes = webpackRuntime.substring(
        webpackRuntime.indexOf("({0") + 1
      );
      final var suffixes = webpackRuntime.substring(
        webpackRuntime.indexOf("+\"-\"+{0") + 5
      );
      final var suffixesMap = (Map<?, ?>) chromeDriver.executeScript(
        "return %s;".formatted(
            suffixes.substring(0, suffixes.indexOf("}[") + 1)
          )
      );
      String linkFileContent;
      try {
        linkFileContent =
          (
            (Map<?, ?>) chromeDriver.executeScript(
              "return %s;".formatted(
                  prefixes.substring(0, prefixes.indexOf("}[") + 1)
                )
            )
          ).entrySet()
            .parallelStream()
            .filter(entry -> {
              final var value = (String) entry.getValue();
              return (
                !(value).contains("component") && !value.contains("style")
              );
            })
            .map(entry ->
              "%s-%s".formatted(
                  entry.getValue(),
                  suffixesMap.get(entry.getKey())
                )
            )
            .map(filename -> {
              try {
                return HttpBuilderFactory
                  .getBuilder()
                  .build()
                  .send(
                    HttpBuilderFactory.buildRequest(
                      UrlBuilder.build(
                        siteUrl.getProtocol(),
                        siteUrl.getHost(),
                        "%s/%s.js".formatted(link, filename)
                      )
                        .toString()
                    ),
                    HttpResponse.BodyHandlers.ofString()
                  )
                  .body();
              } catch (IOException e) {
                throw new WrappedException(e);
              } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new WrappedException(e);
              }
            })
            .filter(fileContent -> fileContent.contains(DISCOVER_MEDIA_FILENAME)
            )
            .findFirst()
            .orElseThrow(() ->
              new NewVersionNotFoundException(
                "Discovery Media filename was not found."
              )
            );
      } catch (NewVersionNotFoundException e) {
        throw new WrappedException(e);
      }
      linkFileContent =
        linkFileContent.substring(
          linkFileContent.indexOf(DISCOVER_MEDIA_FILENAME) +
          DISCOVER_MEDIA_FILENAME.length()
        );
      setNewVersion(
        linkFileContent.substring(0, linkFileContent.indexOf(".7z"))
      );
    });
  }
}
