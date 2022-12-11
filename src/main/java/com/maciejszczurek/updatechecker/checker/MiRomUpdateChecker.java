package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.MI_ROM;

import com.maciejszczurek.updatechecker.application.NewVersionNotFoundException;
import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import com.maciejszczurek.updatechecker.util.UpdateCheckerUtils;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;
import lombok.Setter;

@ApplicationType(MI_ROM)
public class MiRomUpdateChecker extends UpdateChecker implements NameSetter {

  @Setter
  private String name;

  public MiRomUpdateChecker(final String siteUrl, final String currentVersion) {
    super(siteUrl, currentVersion);
  }

  @Override
  public void checkUpdate() throws IOException, InterruptedException {
    final String phoneId = getSiteUrl()
      .substring(getSiteUrl().lastIndexOf('/') + 1);

    setNewVersion(
      Optional
        .ofNullable(
          UpdateCheckerUtils
            .readTree(
              URI
                .create(
                  "https://sgp-api.buy.mi.com/bbs/api/global/phone/getdevicelist?phone_id=%s".formatted(
                      phoneId
                    )
                )
                .toURL()
            )
            .get("data")
            .get("device_data")
            .get("device_list")
            .get(name)
        )
        .orElseThrow(() ->
          new NewVersionNotFoundException(
            "The device with the specified name cannot be found."
          )
        )
        .get("stable_rom")
        .get("version")
        .textValue()
        .substring(1)
    );
  }
}
