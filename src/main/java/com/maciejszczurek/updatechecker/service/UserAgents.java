package com.maciejszczurek.updatechecker.service;

import com.maciejszczurek.updatechecker.checker.UserAgentGenerationException;
import com.maciejszczurek.updatechecker.checker.util.UserAgentGeneratorUtils;
import java.io.IOException;
import java.util.Map;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;

@UtilityClass
@Log4j2
public class UserAgents {

  @Getter
  private static String userAgent;

  @Getter
  private static Map<String, String> uaClientHints;

  public static void generateUserAgent() {
    if (userAgent != null) {
      return;
    }

    synchronized (UserAgents.class) {
      try {
        userAgent = UserAgentGeneratorUtils.generateChromeUserAgent();
        uaClientHints = UserAgentGeneratorUtils.generateUaClientHints();
      } catch (IOException e) {
        throw new UserAgentGenerationException(e);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new UserAgentGenerationException(e);
      }
    }

    log.info("Current user-agent is: {}", userAgent);
    log.info("Current UA client hints are: {}", uaClientHints);
  }
}
