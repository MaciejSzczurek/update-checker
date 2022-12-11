package com.maciejszczurek.updatechecker.service;

import static org.mockito.Mockito.mock;

import com.maciejszczurek.updatechecker.application.model.Application;
import com.maciejszczurek.updatechecker.application.model.ApplicationType;
import com.maciejszczurek.updatechecker.checker.DobreprogramyUpdateChecker;
import com.maciejszczurek.updatechecker.checker.NsaneDownUpdateChecker;
import com.maciejszczurek.updatechecker.cookie.service.CookieHolder;
import java.net.CookieHandler;
import java.net.CookieManager;
import lombok.NoArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.transaction.PlatformTransactionManager;

@NoArgsConstructor
class UpdateCheckerGeneratorTest {

  private AnnotationConfigApplicationContext applicationContext;

  @BeforeEach
  void setUp() {
    final var resourceLoader = new PathMatchingResourcePatternResolver();

    applicationContext = new AnnotationConfigApplicationContext();

    applicationContext.registerBean(
      CookieHolder.class,
      () ->
        new CookieHolder(
          null,
          new CookieManager(),
          mock(PlatformTransactionManager.class)
        )
    );
    applicationContext.registerBean(CookieHandler.class, CookieManager::new);
    applicationContext.registerBean(
      UpdateCheckerFactory.class,
      applicationContext,
      resourceLoader
    );
    applicationContext.refresh();
  }

  @AfterEach
  void tearDown() {
    applicationContext.close();
  }

  @Test
  void getUpdateChecker() {
    final Application application = new Application()
      .setApplicationType(ApplicationType.DOBREPROGRAMY)
      .setSiteUrl("http://example.com")
      .setCurrentVersion("1.0.0");

    final var updateCheckerGenerator = applicationContext.getBean(
      UpdateCheckerFactory.class
    );
    Assertions
      .assertThat(updateCheckerGenerator.getUpdateChecker(application))
      .isInstanceOf(DobreprogramyUpdateChecker.class);
    application.setApplicationType(ApplicationType.NSANE_DOWN);

    Assertions
      .assertThat(updateCheckerGenerator.getUpdateChecker(application))
      .isInstanceOf(NsaneDownUpdateChecker.class);
  }
}
