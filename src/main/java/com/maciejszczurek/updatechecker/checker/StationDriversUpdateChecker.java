package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.STATION_DRIVERS;

import com.maciejszczurek.updatechecker.application.NewVersionNotFoundException;
import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.Setter;

@ApplicationType(STATION_DRIVERS)
public class StationDriversUpdateChecker
  extends UpdateChecker
  implements NameSetter {

  @Setter
  private String name;

  public StationDriversUpdateChecker(
    final String siteUrl,
    final String currentVersion
  ) {
    super(siteUrl, currentVersion);
  }

  @Override
  public void checkUpdate() throws IOException {
    final var siteUrl = new URL(getSiteUrl());

    if (
      Stream
        .concat(
          Arrays
            .stream(
              Optional.ofNullable(siteUrl.getQuery()).orElse("").split("&")
            )
            .map(s -> s.split("=")),
          Arrays
            .stream(
              Optional.ofNullable(siteUrl.getPath()).orElse("").split("/")
            )
            .map(s -> s.split(","))
        )
        .filter(entry -> entry.length == 2)
        .noneMatch(entry -> entry[0].equals("orderby") && entry[1].equals("4"))
    ) {
      throw new NewVersionNotFoundException(
        "There is no sorting in the address or the sorting is not by date."
      );
    }

    setNewVersion(
      getJsoupConnectionInstance()
        .timeout((int) Duration.ofMinutes(2).toMillis())
        .get()
        .select(
          "#remositoryfilelisting > div:nth-child(1) > h3.remositoryfileleft > a"
        )
        .text()
        .replace(name + " ", "")
        .replace("Version ", "")
        .replace(" WHQL", "")
    );
  }
}
