package com.maciejszczurek.updatechecker.checker.util;

import static org.apache.commons.lang3.SystemUtils.IS_OS_LINUX;
import static org.apache.commons.lang3.SystemUtils.IS_OS_MAC;
import static org.apache.commons.lang3.SystemUtils.IS_OS_WINDOWS;
import static org.apache.commons.lang3.SystemUtils.OS_ARCH;
import static org.apache.commons.lang3.SystemUtils.OS_NAME;
import static org.apache.commons.lang3.SystemUtils.OS_VERSION;

import com.maciejszczurek.updatechecker.util.UpdateCheckerUtils;
import com.sun.jna.Native;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.ArchUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

@UtilityClass
public class UserAgentGeneratorUtils {

  private static final boolean IS_OS_POSIX = IS_OS_MAC || IS_OS_LINUX;
  private static final String[] GREASED_CHARS = new String[] {
    " ",
    "(",
    ":",
    "-",
    ".",
    "/",
    ")",
    ";",
    "=",
    "?",
    "_",
  };
  private static final String[] GREASED_VERSIONS = new String[] {
    "8",
    "99",
    "24",
  };
  private String chromeMajorVersion;

  @Contract(pure = true)
  @NotNull
  @SuppressWarnings("java:S3776")
  public String getOsInfo() {
    var platform = IS_OS_MAC ? "Macintosh; " : "";

    var cpuType = "";
    if (IS_OS_MAC) {
      cpuType = "Intel";
    } else if (IS_OS_WINDOWS) {
      if (ArchUtils.getProcessor().is64Bit()) {
        cpuType = "Win64; x64";
      } else if (ArchUtils.getProcessor().isIA64()) {
        cpuType = "Win64; IA64";
      }
    } else if (IS_OS_POSIX) {
      cpuType = ArchUtils.getProcessor().is64Bit() &&
        Native.POINTER_SIZE == Integer.BYTES
        ? "i686 (x86_64)"
        : OS_ARCH;
    }

    var osVersion = "";
    if (IS_OS_MAC || IS_OS_WINDOWS) {
      final var splittedVersion = Arrays.stream(
        OS_VERSION.split("\\.")
      ).collect(Collectors.toCollection(ArrayList::new));

      if (IS_OS_WINDOWS) {
        osVersion = splittedVersion
          .stream()
          .limit(2)
          .collect(Collectors.joining("."));
      } else {
        if (Integer.parseInt(splittedVersion.get(0)) > 10) {
          splittedVersion.set(0, "10");
          splittedVersion.set(1, "15");
          splittedVersion.set(2, "7");
        }

        osVersion = splittedVersion
          .stream()
          .limit(3)
          .collect(Collectors.joining("_"));
      }
    }

    var osInfo = "";
    if (IS_OS_WINDOWS) {
      if (cpuType.isEmpty()) {
        osInfo = "Windows NT %s".formatted(osVersion);
      } else {
        osInfo = "Windows NT %s; %s".formatted(osVersion, cpuType);
      }
    } else if (IS_OS_MAC) {
      osInfo = "%s Mac OS X %s".formatted(cpuType, osVersion);
    } else if (IS_OS_POSIX) {
      osInfo = "%s %s".formatted(OS_NAME, cpuType);
    }

    return "%s%s".formatted(platform, osInfo);
  }

  public Map<String, String> generateBrandVersionsList(
    final String majorVersion
  ) {
    final var seed = Integer.parseInt(majorVersion);

    var brandVersions = LinkedHashMap.<String, String>newLinkedHashMap(2);
    brandVersions.put(
      "Not%sA%sBrand".formatted(
          GREASED_CHARS[seed % GREASED_CHARS.length],
          GREASED_CHARS[(seed + 1) % GREASED_CHARS.length]
        ),
      GREASED_VERSIONS[seed % GREASED_VERSIONS.length]
    );
    brandVersions.put("Chromium", majorVersion);

    return brandVersions;
  }

  @NotNull
  @Unmodifiable
  public Map<String, String> generateUaClientHints()
    throws IOException, InterruptedException {
    final String platform;
    if (IS_OS_LINUX) {
      platform = "Linux";
    } else if (IS_OS_WINDOWS) {
      platform = "Windows";
    } else if (IS_OS_MAC) {
      platform = "macOS";
    } else {
      platform = "Unknown";
    }

    return Map.of(
      "sec-ch-ua",
      generateBrandVersionsList(getChromeMajorVersion())
        .entrySet()
        .stream()
        .map(entry ->
          "\"%s\";v=\"%s\"".formatted(entry.getKey(), entry.getValue())
        )
        .collect(Collectors.joining(", ")),
      "sec-ch-ua-mobile",
      "?0",
      "sec-ch-ua-platform",
      "\"%s\"".formatted(platform)
    );
  }

  @NotNull
  public String getChromeMajorVersion()
    throws IOException, InterruptedException {
    if (chromeMajorVersion == null) {
      var chromeVersion = UpdateCheckerUtils.readTree(
        URI.create(
          "https://chromium.woolyss.com/api/v3/?os=windows&out=json&type=stable-codecs-sync"
        ).toURL()
      )
        .get("chromium")
        .get("windows")
        .get("version")
        .textValue();
      chromeMajorVersion = chromeVersion.substring(
        0,
        chromeVersion.indexOf('.')
      );
    }

    return chromeMajorVersion;
  }

  public String generateChromeUserAgent()
    throws IOException, InterruptedException {
    return "Mozilla/5.0 (%s) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/%s.0.0.0 Safari/537.36".formatted(
        getOsInfo(),
        getChromeMajorVersion()
      );
  }
}
