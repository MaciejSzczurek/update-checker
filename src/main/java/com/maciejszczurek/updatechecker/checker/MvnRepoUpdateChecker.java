package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.MVN_REPO;

import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import com.maciejszczurek.updatechecker.checker.util.MavenVersionReaderUtility;
import java.io.IOException;

@ApplicationType(MVN_REPO)
public class MvnRepoUpdateChecker extends UpdateChecker {

  public MvnRepoUpdateChecker(
    final String siteUrl,
    final String currentVersion
  ) {
    super(siteUrl, currentVersion);
  }

  @Override
  public void checkUpdate() throws IOException {
    setNewVersion(
      MavenVersionReaderUtility.readVersion(
        getJsoupConnectionInstance(
          "https://repo.maven.apache.org/maven2/%s/maven-metadata.xml".formatted(
              getSiteUrl().contains("jakarta.validation/jakarta.validation-api")
                ? "jakarta/validation/jakarta.validation-api"
                : getSiteUrl()
                  .replace("https://mvnrepository.com/artifact/", "")
                  .replace('.', '/')
            )
        )
          .get()
      )
    );
  }
}
