package com.maciejszczurek.updatechecker.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

@UtilityClass
@Log4j2
public class UpdateCheckerUtils {

  @Getter(lazy = true)
  private final ObjectMapper objectMapper = generateObjectMapper();

  @Contract(" -> new")
  @NotNull
  private synchronized ObjectMapper generateObjectMapper() {
    return new ObjectMapper();
  }

  public JsonNode readTree(@NotNull final URL url)
    throws IOException, InterruptedException {
    return getObjectMapper().readTree(getInputStreamBody(url));
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
    return getObjectMapper().readTree(inputStream);
  }

  public JsonNode readTree(final String content)
    throws JsonProcessingException {
    return getObjectMapper().readTree(content);
  }

  public <T> T readValue(final URL url, final TypeReference<T> typeReference)
    throws IOException, InterruptedException {
    return getObjectMapper().readValue(getInputStreamBody(url), typeReference);
  }
}
