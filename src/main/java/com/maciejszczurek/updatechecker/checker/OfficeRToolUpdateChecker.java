package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.OFFICE_R_TOOL;

import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import java.io.IOException;
import java.net.CookieManager;
import lombok.Setter;

@ApplicationType(OFFICE_R_TOOL)
public class OfficeRToolUpdateChecker extends UpdateChecker {

  @Setter
  private CookieManager cookieManager;

  public OfficeRToolUpdateChecker(
    final String siteUrl,
    final String currentVersion
  ) {
    super(siteUrl, currentVersion);
  }

  @Override
  public void checkUpdate() throws IOException {
    var article = getJsoupConnectionInstance()
      .cookieStore(cookieManager.getCookieStore())
      .get()
      .select("#post-1702724")
      .text();
    article = article.substring(article.indexOf("Release Date  :: ") + 17);
    setNewVersion(article.substring(0, article.indexOf('\n')));
  }
}
