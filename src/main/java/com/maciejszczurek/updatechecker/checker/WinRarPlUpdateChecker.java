package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.WIN_RAR_PL;

import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import java.io.IOException;
import java.util.Objects;
import org.jsoup.nodes.Element;

@ApplicationType(WIN_RAR_PL)
public class WinRarPlUpdateChecker extends UpdateChecker {

  public WinRarPlUpdateChecker(
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
        .select("tr")
        .stream()
        .filter(element -> Objects.nonNull(element.selectFirst("td > a > b")))
        .filter(element ->
          element.selectFirst("td > a").attr("href").endsWith(".exe")
        )
        .filter(element ->
          Objects.nonNull(element.selectFirst("td:nth-child(2)"))
        )
        .filter(element ->
          element
            .selectFirst("td:nth-child(1) > a > b")
            .text()
            .equals("Polish (64 bit)")
        )
        .map(element -> element.selectFirst("td:nth-child(2)"))
        .map(Element::text)
        .findFirst()
        .orElseThrow(NewVersionEmptyException::new)
    );
  }
}
