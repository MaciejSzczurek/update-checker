package com.maciejszczurek.updatechecker.chrome.config;

import lombok.RequiredArgsConstructor;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.target.CommonsPool2TargetSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ChromeServicePoolConfiguration {

  @Value("${webdriver.pool-size:4}")
  private Integer poolSize;

  @Bean
  public CommonsPool2TargetSource chromeTargetPool() {
    final var targetSource = new CommonsPool2TargetSource();

    targetSource.setMaxSize(poolSize);
    targetSource.setTargetBeanName("chromeDriverHolder");

    return targetSource;
  }

  @Bean
  public ProxyFactoryBean chromeDriverFactory() {
    final var factoryBean = new ProxyFactoryBean();

    factoryBean.setTargetSource(chromeTargetPool());

    return factoryBean;
  }
}
