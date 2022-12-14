package com.maciejszczurek;

import com.pivovarit.function.ThrowingSupplier;
import com.pivovarit.function.exception.WrappedException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;
import java.util.stream.LongStream;
import java.util.zip.ZipFile;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.TaskAction;
import org.jetbrains.annotations.NotNull;

@CacheableTask
public abstract class DownloadChromeDriverTask extends DefaultTask {

  @Input
  abstract Property<String> getVersion();

  @Internal
  abstract Property<Integer> getThreadsNumber();

  private static final Pattern cdcPattern = Pattern.compile("cdc_.{22}");

  @NotNull
  private static String generateCdc() {
    final var cdc = new StringBuilder(26);

    return cdc
      .append(RandomStringUtils.randomAlphabetic(2).toLowerCase())
      .append(cdc.charAt(0))
      .append('_')
      .append(RandomStringUtils.randomAlphanumeric(22))
      .toString();
  }

  @TaskAction
  void downloadChromeDriver() throws IOException {
    final String os;
    if (SystemUtils.IS_OS_LINUX) {
      os = "linux64";
    } else if (SystemUtils.IS_OS_MAC) {
      os = "mac64" + (SystemUtils.OS_ARCH.equals("arm64") ? "_m1" : "");
    } else {
      os = "win32";
    }

    final Path chromeDriverPath;
    final var tempFile = getTemporaryDir()
      .toPath()
      .resolve("chromedriver.zip")
      .toFile();
    FileUtils.copyURLToFile(
      new URL(
        "https://chromedriver.storage.googleapis.com/%s/chromedriver_%s.zip".formatted(
            getVersion().get(),
            os
          )
      ),
      tempFile
    );

    try (var zipFile = new ZipFile(tempFile)) {
      final var zipEntry = zipFile
        .stream()
        .filter(zipEntry1 -> zipEntry1.getName().startsWith("chromedriver"))
        .findFirst()
        .orElseThrow();
      chromeDriverPath =
        getProject()
          .getProjectDir()
          .toPath()
          .resolve(Path.of("libs", "chromedriver", zipEntry.getName()));

      FileUtils.copyInputStreamToFile(
        zipFile.getInputStream(zipEntry),
        chromeDriverPath.toFile()
      );
    }

    Files.delete(tempFile.toPath());

    patchChrome(chromeDriverPath);
  }

  private void patchChrome(@NotNull Path chromeDriverPath) throws IOException {
    final var chromeDriverSize = chromeDriverPath.toFile().length();
    final var chromeDriverFragment = chromeDriverSize >> 4;
    final var newLinesPositionsFuture = new ArrayList<CompletableFuture<List<lineFragment>>>();
    try (
      var executor = Executors.newFixedThreadPool(
        getThreadsNumber()
          .getOrElse(Runtime.getRuntime().availableProcessors() << 2)
      )
    ) {
      LongStream
        .iterate(0L, i -> i < chromeDriverSize, i -> i + chromeDriverFragment)
        .forEach(start ->
          newLinesPositionsFuture.add(
            CompletableFuture.supplyAsync(
              ThrowingSupplier.sneaky(() ->
                getLineFragments(
                  chromeDriverPath,
                  start,
                  start + chromeDriverFragment
                )
              ),
              executor
            )
          )
        );

      CompletableFuture
        .allOf(newLinesPositionsFuture.toArray(CompletableFuture[]::new))
        .join();
    }

    var newLinesPositions = newLinesPositionsFuture
      .stream()
      .flatMap(listCompletableFuture -> {
        try {
          return listCompletableFuture.get().stream();
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          throw new WrappedException(e);
        } catch (ExecutionException e) {
          throw new WrappedException(e);
        }
      })
      .toList();

    try (var file = new RandomAccessFile(chromeDriverPath.toFile(), "rw")) {
      final var newCdc = generateCdc().getBytes(StandardCharsets.US_ASCII);

      for (lineFragment lineFragment : newLinesPositions) {
        file.seek(lineFragment.index);

        var line = new byte[(int) lineFragment.size];
        file.read(line);

        final var matcher = cdcPattern.matcher(
          new String(line, StandardCharsets.US_ASCII)
        );
        while (matcher.find()) {
          System.arraycopy(newCdc, 0, line, matcher.start(), newCdc.length);
        }

        file.seek(lineFragment.index);
        file.write(line);
      }
    }
  }

  @NotNull
  private List<lineFragment> getLineFragments(
    final Path chromeDriverPath,
    final long start,
    final long end
  ) throws IOException {
    final var positions = new ArrayList<lineFragment>();
    try (var file = new RandomAccessFile(chromeDriverPath.toFile(), "r")) {
      file.seek(start);
      String line;

      while (file.getFilePointer() < end && (line = file.readLine()) != null) {
        if (line.contains("cdc_")) {
          var lineBytesLength = line.getBytes(StandardCharsets.US_ASCII).length;
          positions.add(
            new lineFragment(
              file.getFilePointer() - lineBytesLength - 1,
              lineBytesLength
            )
          );
        }
      }
    }
    return positions;
  }

  private record lineFragment(long index, long size) {}
}
