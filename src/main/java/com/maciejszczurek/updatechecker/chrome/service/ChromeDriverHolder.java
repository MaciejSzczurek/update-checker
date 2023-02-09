package com.maciejszczurek.updatechecker.chrome.service;

import com.maciejszczurek.updatechecker.option.service.OptionService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
public class ChromeDriverHolder {

  private static final Pattern versionPattern = Pattern.compile(
    "([\\d.]+) \\([\\da-f]{40}-refs/"
  );
  private final OptionService optionService;
  private final ResourceLoader resourceLoader;
  private ChromeDriver driver;

  @NotNull
  private static String getChromeDriverVersion(
    @NotNull final Resource resource
  ) throws IOException {
    try (var file = resource.getInputStream()) {
      file.skipNBytes(0x9B_0000);

      String version = "";
      var scanner = new Scanner(file, StandardCharsets.US_ASCII);

      while (scanner.hasNext()) {
        var matcher = versionPattern.matcher(scanner.nextLine());

        if (matcher.find()) {
          version = matcher.group(1);
          break;
        }
      }

      if (version.isEmpty()) {
        throw new ChromeDriverVersionNotFoundException();
      }

      return version;
    }
  }

  @Transactional
  @PostConstruct
  public void initialize() throws IOException {
    final var chromeDriverPath = resourceLoader
      .getResource("file:chromedriver.exe")
      .getFile()
      .toPath();

    final var chromeDriverResource = resourceLoader.getResource(
      "classpath:chromedriver.exe"
    );

    if (
      !Files.exists(chromeDriverPath) ||
      !getChromeDriverVersion(chromeDriverResource)
        .equals(optionService.getOption("chrome-driver-version", ""))
    ) {
      FileUtils.copyInputStreamToFile(
        chromeDriverResource.getInputStream(),
        chromeDriverPath.toFile()
      );
      optionService.setOption(
        "chrome-driver-version",
        getChromeDriverVersion(chromeDriverResource)
      );
    }

    driver =
      new ChromeDriver(
        new ChromeDriverService.Builder().withSilent(true).build(),
        new ChromeOptions()
          .addArguments(
            "--disable-blink-features=AutomationControlled",
            "--headless=new",
            "--window-size=1920,1080",
            "--start-maximized"
          )
      );

    final var driverVersion =
      (
        (String) (
          (Map<?, ?>) driver.getCapabilities().getCapability("chrome")
        ).get("chromedriverVersion")
      ).split("\\.")[0];

    if (
      !driver
        .getCapabilities()
        .getBrowserVersion()
        .split("\\.")[0].equals(driverVersion)
    ) {
      quit();

      throw new IncorrectChromeDriverVersion();
    }

    driver.executeCdpCommand(
      "Page.addScriptToEvaluateOnNewDocument",
      Map.of(
        "source",
        """
                  Object.defineProperty(window, "navigator", {
                    value: new Proxy(navigator, {
                    has: (target, key) => (key === "webdriver" ? false : key in target),
                      get: (target, key) =>
                        key === "webdriver"
                          ? undefined
                          : typeof target[key] === "function"
                          ? target[key].bind(target)
                          : target[key]
                    })
                  });
                  
                  Object.defineProperty(Notification, "permission", {
                    configurable: true,
                    enumerable: true,
                    get: () => {
                      return "unknown";
                    }
                  });
                """
      )
    );
    driver.executeCdpCommand(
      "Network.setUserAgentOverride",
      Map.of(
        "userAgent",
        ((String) driver.executeScript("return navigator.userAgent")).replace(
            "Headless",
            ""
          )
      )
    );
  }

  @PreDestroy
  public void quit() {
    driver.quit();
  }

  public void run(@NotNull final Consumer<ChromeDriver> consumer) {
    consumer.accept(driver);
  }
}
