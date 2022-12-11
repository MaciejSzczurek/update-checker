package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.CHROME_DRIVER;

import com.maciejszczurek.updatechecker.application.NewVersionNotFoundException;
import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import com.maciejszczurek.updatechecker.checker.util.UserAgentGeneratorUtils;
import com.pivovarit.function.ThrowingPredicate;
import java.io.IOException;
import org.jsoup.nodes.Element;

@ApplicationType(CHROME_DRIVER)
public class ChromeDriverUpdateChecker extends UpdateChecker {

  public ChromeDriverUpdateChecker(
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
        .select("a.XqQF9c")
        .stream()
        .map(Element::text)
        .filter(element -> element.startsWith("ChromeDriver "))
        .map(element -> element.replace("ChromeDriver ", ""))
        .filter(
          ThrowingPredicate.unchecked(element ->
            element
              .substring(0, element.indexOf('.'))
              .equals(UserAgentGeneratorUtils.getChromeMajorVersion())
          )
        )
        .findFirst()
        .orElseThrow(() ->
          new NewVersionNotFoundException(
            "Cannot find new version of Chrome Driver."
          )
        )
    );
  }
}
