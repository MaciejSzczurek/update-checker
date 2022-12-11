package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.MAJOR_GEEKS;

import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import java.io.IOException;

@ApplicationType(MAJOR_GEEKS)
public class MajorGeeksUpdateChecker extends UpdateChecker {

  public MajorGeeksUpdateChecker(
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
        .select("meta[itemprop='softwareVersion']")
        .attr("content")
        .replaceFirst(" Final$", "")
    );
  }
}
