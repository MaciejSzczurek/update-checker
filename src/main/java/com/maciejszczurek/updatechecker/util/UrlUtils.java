package com.maciejszczurek.updatechecker.util;

import java.io.IOException;
import java.util.ArrayList;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.SystemUtils;

@UtilityClass
public class UrlUtils {

  public void openUrl(final String url) throws IOException {
    var command = new ArrayList<String>(3);

    if (SystemUtils.IS_OS_WINDOWS) {
      command.add("rundll32");
      command.add("url.dll,FileProtocolHandler");
    } else if (SystemUtils.IS_OS_MAC) {
      command.add("open");
    } else if (SystemUtils.IS_OS_LINUX) {
      command.add("xdg-open");
    }

    if (!command.isEmpty()) {
      command.add(url);
      Runtime.getRuntime().exec(command.toArray(String[]::new));
    }
  }
}
