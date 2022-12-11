package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.HTTP_APACHE_LIST_LAST_MODIFIED;

import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import java.io.IOException;

@ApplicationType(HTTP_APACHE_LIST_LAST_MODIFIED)
public class HttpApacheListLastModifiedUpdateChecker extends UpdateChecker {

  public HttpApacheListLastModifiedUpdateChecker(
    final String siteUrl,
    final String currentVersion
  ) {
    super(siteUrl, currentVersion);
  }

  @Override
  public void checkUpdate() throws IOException {
    final var document = getJsoupConnectionInstance().get();
    final var select = document.select(
      "#indexlist > tbody > tr:nth-child(4) > td.indexcollastmod"
    );

    if (!select.isEmpty()) {
      setNewVersion(select.text());

      return;
    }

    final var tables = document.select("table");

    if (tables.size() == 3) {
      setNewVersion(
        tables.get(2).selectFirst("tr:nth-child(4) > td:nth-child(3)").text()
      );

      return;
    }

    var body = document.html();
    var beginFromIndex = body.indexOf(
      "<img src=\"/icons/folder.gif\" alt=\"[DIR]\">"
    );

    if (beginFromIndex == -1) {
      beginFromIndex =
        body.indexOf("<img src=\"/icons/layout.gif\" alt=\"[   ]\">");
    }

    if (beginFromIndex == -1) {
      beginFromIndex =
        body.indexOf("<img src=\"/icons/unknown.gif\" alt=\"[   ]\">");
    }

    // You shouldn't connect that into one statement
    body = body.substring(beginFromIndex, body.indexOf('\n', beginFromIndex));
    body = body.substring(body.indexOf("</a>") + 4).trim();

    setNewVersion(body.substring(0, body.lastIndexOf(' ')).trim());
  }
}
