package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.POSTGRES;
import static java.net.http.HttpClient.Redirect;
import static java.net.http.HttpClient.Version;

import com.maciejszczurek.updatechecker.application.NewVersionNotFoundException;
import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.stream.IntStream;

@ApplicationType(POSTGRES)
public class PostgresUpdateChecker extends UpdateChecker {

  public PostgresUpdateChecker(
    final String siteUrl,
    final String currentVersion
  ) {
    super(siteUrl, currentVersion);
  }

  @Override
  public void checkUpdate() throws IOException, InterruptedException {
    final var table = getJsoupConnectionInstance()
      .get()
      .select("#main-content table");
    final var headerColumns = table.select("thead > tr > th");

    final var windowsUrl = table
      .select("tbody > tr:nth-child(1) > td")
      .eq(
        IntStream
          .range(0, headerColumns.size())
          .filter(index ->
            headerColumns.get(index).text().equals("Windows x86-64")
          )
          .findFirst()
          .orElseThrow(() ->
            new NewVersionNotFoundException(
              "Cannot find column with Windows x64 installer."
            )
          )
      )
      .select("a")
      .attr("href");

    setNewVersion(
      HttpClient
        .newBuilder()
        .followRedirects(Redirect.NEVER)
        .version(Version.HTTP_2)
        .build()
        .send(
          HttpRequest.newBuilder().uri(URI.create(windowsUrl)).build(),
          HttpResponse.BodyHandlers.discarding()
        )
        .headers()
        .firstValue("location")
        .orElseThrow(() ->
          new NewVersionNotFoundException(
            "Location with Postgres installer file is empty."
          )
        )
        .replace("https://get.enterprisedb.com/postgresql/postgresql-", "")
        .replace("-windows-x64.exe", "")
    );
  }
}
