package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.GPLDL;

import com.maciejszczurek.updatechecker.application.NewVersionNotFoundException;
import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import java.io.IOException;
import lombok.Setter;

@ApplicationType(GPLDL)
public class GpldlUpdateChecker extends UpdateChecker implements NameSetter {

  @Setter
  private String name;

  public GpldlUpdateChecker(final String siteUrl, final String currentVersion) {
    super(siteUrl, currentVersion);
  }

  @Override
  public void checkUpdate() throws IOException {
    setNewVersion(
      getJsoupConnectionInstance()
        .get()
        .select("div.sortcontainer > div")
        .parallelStream()
        .filter(element -> element.dataset().get("sortname").startsWith(name))
        .findFirst()
        .orElseThrow(() ->
          new NewVersionNotFoundException("Cannot find selected plugin.")
        )
        .select("div.inf > span.info:nth-child(1)")
        .text()
    );
  }
}
