package com.maciejszczurek.updatechecker.chrome.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.maciejszczurek.updatechecker.option.service.OptionService;
import com.maciejszczurek.updatechecker.service.UserAgents;
import java.io.IOException;
import java.time.Duration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.RepeatedTest;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.core.io.DefaultResourceLoader;

public class ChromeServiceHolderTest {

  public static final String CHROME_DRIVER_VERSION = "111.0.5563.64";
  private static ChromeDriverHolder chromeService;

  @BeforeAll
  static void beforeAll() throws IOException {
    UserAgents.generateUserAgent();

    final var optionService = mock(OptionService.class);
    final var resourceLoader = new DefaultResourceLoader();

    when(optionService.getOption("chrome-driver-version", ""))
      .thenReturn(CHROME_DRIVER_VERSION);

    chromeService = new ChromeDriverHolder(optionService, resourceLoader);
    chromeService.initialize();
  }

  @AfterAll
  static void afterAll() {
    chromeService.quit();
  }

  @RepeatedTest(10)
  void chromeServiceTest() {
    chromeService.run(chromeDriver -> {
      chromeDriver.get("https://fitgirl-repacks.site/euro-truck-simulator-2/");
      new WebDriverWait(chromeDriver, Duration.ofMinutes(1))
        .until(driver1 ->
          !driver1.findElements(By.className("entry-title")).isEmpty()
        );
      assertThat(chromeDriver.findElements(By.className("entry-title")))
        .isNotEmpty();
    });
  }
}
