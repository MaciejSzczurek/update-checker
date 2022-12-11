package com.maciejszczurek.updatechecker.checker;

import com.maciejszczurek.updatechecker.service.UserAgents;
import java.io.IOException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.Contract;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

@Log4j2
public abstract class UpdateChecker {

  @Getter
  @Setter
  private String siteUrl;

  @Getter
  @Setter
  private String updateUrl;

  @Getter
  @Setter
  private String currentVersion;

  @Getter
  private String newVersion;

  @Contract(pure = true)
  protected UpdateChecker(final String siteUrl, final String currentVersion) {
    this.currentVersion = currentVersion;
    this.siteUrl = siteUrl;
  }

  protected void setNewVersion(final String newVersion) {
    this.newVersion = newVersion;
  }

  public boolean isUpdate() throws NewVersionEmptyException {
    if (newVersion == null || newVersion.isEmpty()) {
      throw new NewVersionEmptyException();
    }

    return !currentVersion.equals(newVersion);
  }

  public void update() {
    currentVersion = newVersion;
  }

  public void setSameVersion() {
    newVersion = currentVersion;
  }

  public abstract void checkUpdate() throws IOException, InterruptedException;

  protected Connection getJsoupConnectionInstance() {
    return getJsoupConnectionInstance(siteUrl);
  }

  protected Connection getJsoupConnectionInstance(final String siteUrl) {
    return Jsoup
      .connect(siteUrl)
      .userAgent(UserAgents.getUserAgent())
      .headers(UserAgents.getUaClientHints());
  }
}
