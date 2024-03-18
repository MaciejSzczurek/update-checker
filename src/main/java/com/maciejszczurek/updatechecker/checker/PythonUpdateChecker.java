package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.PYTHON;

import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import com.maciejszczurek.updatechecker.http.HttpBuilderFactory;
import java.io.IOException;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.jsoup.Jsoup;

@ApplicationType(PYTHON)
public class PythonUpdateChecker extends UpdateChecker {

  private static final String TEXT_TO_FIND = "Python ";

  public PythonUpdateChecker(
    final String siteUrl,
    final String currentVersion
  ) {
    super(siteUrl, currentVersion);
  }

  @Override
  public void checkUpdate() throws IOException, InterruptedException {
    var httpResponse = HttpBuilderFactory.getBuilder()
      .build()
      .send(
        HttpBuilderFactory.buildRequest(getSiteUrl()),
        HttpResponse.BodyHandlers.ofInputStream()
      );
    var isGzipEncoding = httpResponse
      .headers()
      .firstValue("content-encoding")
      .map(contentEncoding -> contentEncoding.equalsIgnoreCase("gzip"))
      .orElse(Boolean.FALSE);
    var text = Jsoup.parse(
      isGzipEncoding
        ? new GzipCompressorInputStream(httpResponse.body())
        : httpResponse.body(),
      StandardCharsets.UTF_8.name(),
      getSiteUrl()
    )
      .selectFirst(
        "#content > div > section > article > ul > li:nth-child(1) > a"
      )
      .text();

    setNewVersion(
      text.substring(text.lastIndexOf(TEXT_TO_FIND) + TEXT_TO_FIND.length())
    );
  }
}
