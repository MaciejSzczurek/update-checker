package com.maciejszczurek.updatechecker.webclient.configuration;

import com.maciejszczurek.updatechecker.service.UserAgents;
import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

@Configuration
class RestClientCustomConfiguration {

  @Bean
  public RestClientCustomizer userAgentRestClientCustomizer() {
    return builder -> {
      builder.defaultHeader(HttpHeaders.USER_AGENT, UserAgents.getUserAgent());
      builder.defaultHeaders(headers ->
        UserAgents.getUaClientHints().forEach(headers::add)
      );
    };
  }
}
