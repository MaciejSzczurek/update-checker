package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.RESPONSE_BODY;

import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import com.maciejszczurek.updatechecker.http.HttpBuilderFactory;
import java.io.IOException;
import java.net.http.HttpResponse;

@ApplicationType(RESPONSE_BODY)
public class ResponseBodyUpdateChecker extends UpdateChecker {

  public ResponseBodyUpdateChecker(
    final String siteUrl,
    final String currentVersion
  ) {
    super(siteUrl, currentVersion);
  }

  @Override
  public void checkUpdate() throws IOException, InterruptedException {
    setNewVersion(
      HttpBuilderFactory
        .getBuilder()
        .build()
        .send(
          HttpBuilderFactory.buildRequest(getSiteUrl()),
          HttpResponse.BodyHandlers.ofString()
        )
        .body()
    );
  }
}
