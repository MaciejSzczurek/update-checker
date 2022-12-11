package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.IMAGE_MAGICK_PHP_WINDOWS;

import com.maciejszczurek.updatechecker.application.NewVersionNotFoundException;
import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import java.io.IOException;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

@ApplicationType(IMAGE_MAGICK_PHP_WINDOWS)
public class ImageMagickPhpWindowsUpdateChecker extends UpdateChecker {

  private static final String PHP_VERSION_COLUMN = "td:nth-child(1)";

  public ImageMagickPhpWindowsUpdateChecker(
    final String siteUrl,
    final String currentVersion
  ) {
    super(siteUrl, currentVersion);
  }

  @Override
  public void checkUpdate() throws IOException {
    final Elements table = getJsoupConnectionInstance()
      .get()
      .select("#versions-table > tbody > tr");

    final String currentPhpVersion = table
      .stream()
      .map(element -> element.select(PHP_VERSION_COLUMN).text())
      .max(String::compareTo)
      .orElseThrow(() ->
        new NewVersionNotFoundException(
          "The latest version of PHP cannot be found in the table."
        )
      );

    final Element foundRow = table
      .stream()
      .filter(element ->
        element.select(PHP_VERSION_COLUMN).text().equals(currentPhpVersion)
      )
      .filter(element -> element.select("td:nth-child(2)").text().equals("Yes"))
      .filter(element -> element.select("td:nth-child(3)").text().equals("x64"))
      .findFirst()
      .orElseThrow(() ->
        new NewVersionNotFoundException(
          "The latest version was not found in the table."
        )
      );

    final String extensionVersion = foundRow
      .select("td:nth-child(4) > a")
      .text()
      .replace("php_imagick-", "");
    final String imageMagickVersion = foundRow
      .select("td:nth-child(5) > a")
      .text()
      .replace("ImageMagick-", "");

    setNewVersion(
      "%s (%s)".formatted(
          extensionVersion.substring(0, extensionVersion.indexOf('-')),
          imageMagickVersion.isEmpty()
            ? "n/a"
            : imageMagickVersion.substring(0, imageMagickVersion.indexOf('-'))
        )
    );
  }
}
