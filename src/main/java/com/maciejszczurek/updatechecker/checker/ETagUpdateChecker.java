package com.maciejszczurek.updatechecker.checker;

import static com.maciejszczurek.updatechecker.application.model.ApplicationType.E_TAG;

import com.maciejszczurek.updatechecker.application.NewVersionNotFoundException;
import com.maciejszczurek.updatechecker.checker.annotation.ApplicationType;
import java.io.IOException;
import java.util.Optional;
import lombok.Setter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;

@ApplicationType(E_TAG)
public class ETagUpdateChecker extends UpdateChecker {

  @Setter
  private RestClient.Builder restClientBuilder;

  public ETagUpdateChecker(String siteUrl, String currentVersion) {
    super(siteUrl, currentVersion);
  }

  @Override
  public void checkUpdate() throws IOException, InterruptedException {
    setNewVersion(
      Optional.ofNullable(
        restClientBuilder
          .build()
          .get()
          .uri(getSiteUrl())
          .retrieve()
          .toBodilessEntity()
          .getHeaders()
          .get(HttpHeaders.ETAG)
      )
        .flatMap(strings -> Optional.ofNullable(strings.getFirst()))
        .orElseThrow(() -> new NewVersionNotFoundException("E-tag is empty."))
    );
  }
}
