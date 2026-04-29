package com.maciejszczurek.updatechecker.util;

import com.maciejszczurek.updatechecker.http.HttpBuilderFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.http.HttpResponse;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

@UtilityClass
@Log4j2
public class UpdateCheckerUtils {

  @Getter(lazy = true)
  private final JsonMapper jsonMapper = generateJsonMapper();

  @Contract(" -> new")
  @NotNull
  private synchronized JsonMapper generateJsonMapper() {
    return new JsonMapper();
  }

  public JsonNode readTree(@NotNull final URL url)
    throws IOException, InterruptedException {
    return getJsonMapper().readTree(getInputStreamBody(url));
  }

  private InputStream getInputStreamBody(@NotNull final URL url)
    throws IOException, InterruptedException {
    return HttpBuilderFactory
      .getBuilder()
      .build()
      .send(
        HttpBuilderFactory.buildRequest(url.toString()),
        HttpResponse.BodyHandlers.ofInputStream()
      )
      .body();
  }

  public JsonNode readTree(final InputStream inputStream) throws IOException {
    return getJsonMapper().readTree(inputStream);
  }

  public JsonNode readTree(final String content) {
    return getJsonMapper().readTree(content);
  }

  public <T> T readValue(final URL url, final TypeReference<T> typeReference)
    throws IOException, InterruptedException {
    return getJsonMapper().readValue(getInputStreamBody(url), typeReference);
  }
}
