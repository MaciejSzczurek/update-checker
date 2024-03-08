package com.maciejszczurek.updatechecker.util;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UrlBuilder {

  public URL build(String spec) throws MalformedURLException {
    return URI.create(spec).toURL();
  }

  public URL build(String protocol, String host, String path)
    throws MalformedURLException {
    return URI.create("%s://%s%s".formatted(protocol, host, path)).toURL();
  }

  public URL build(String protocol, String host, int port, String path)
    throws MalformedURLException {
    return URI.create(
      "%s://%s:%d%s".formatted(protocol, host, port, path)
    ).toURL();
  }
}
