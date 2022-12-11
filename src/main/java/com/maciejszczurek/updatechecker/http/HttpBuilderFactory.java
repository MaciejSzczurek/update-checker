package com.maciejszczurek.updatechecker.http;

import static java.net.http.HttpClient.Redirect.ALWAYS;
import static java.net.http.HttpClient.Version.HTTP_2;

import com.maciejszczurek.updatechecker.service.UserAgents;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import lombok.experimental.UtilityClass;

@UtilityClass
public class HttpBuilderFactory {

  public HttpClient.Builder getBuilder() {
    return HttpClient.newBuilder().followRedirects(ALWAYS).version(HTTP_2);
  }

  public HttpRequest buildRequest(final String uri) {
    final var builder = HttpRequest.newBuilder();

    if (UserAgents.getUserAgent() != null) {
      builder.header("User-Agent", UserAgents.getUserAgent());
    }

    if (UserAgents.getUaClientHints() != null) {
      UserAgents.getUaClientHints().forEach(builder::header);
    }

    return builder.uri(URI.create(uri)).GET().build();
  }
}
