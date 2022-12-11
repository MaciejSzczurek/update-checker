package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.SOFTWARE_OK;

import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import java.io.IOException;
import lombok.Setter;

@ApplicationType(SOFTWARE_OK)
public class SoftwareOkUpdateChecker
  extends UpdateChecker
  implements NameSetter {

  @Setter
  private String name;

  public SoftwareOkUpdateChecker(
    final String siteUrl,
    final String currentVersion
  ) {
    super(siteUrl, currentVersion);
  }

  @Override
  public void checkUpdate() throws IOException {
    setNewVersion(
      getJsoupConnectionInstance()
        .get()
        .selectFirst("#HOPALA_WEG3 > a:nth-child(4)")
        .text()
        .replace(name + " ", "")
    );
  }
}
