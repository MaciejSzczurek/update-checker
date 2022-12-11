package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.REALTEK_ETHERNET_WINDOWS10;

import com.maciejszczurek.updatechecker.application.NewVersionNotFoundException;
import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import java.io.IOException;

@ApplicationType(REALTEK_ETHERNET_WINDOWS10)
public class RealtekEthernetWindows10UpdateChecker extends UpdateChecker {

  public RealtekEthernetWindows10UpdateChecker(
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
        .select(
          "#clDownloadsFileList_testmytable1 > tbody > tr:nth-child(8) > td > table > tbody > tr"
        )
        .stream()
        .filter(element -> element.select("p").text().startsWith("Win10"))
        .map(element -> element.select("td").eq(1).text())
        .findFirst()
        .orElseThrow(() ->
          new NewVersionNotFoundException(
            "Cannot find new version of Realtek Ethernet Windows 10 driver."
          )
        )
    );
  }
}
