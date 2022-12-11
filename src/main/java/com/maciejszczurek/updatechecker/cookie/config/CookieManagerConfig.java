package com.maciejszczurek.updatechecker.cookie.config;

import java.net.CookieManager;
import java.net.CookieStore;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class CookieManagerConfig {

  @Bean
  public CookieManager cookieManager(final CookieStore cookieStore) {
    return new CookieManager(cookieStore, null);
  }
}
