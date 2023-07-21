package org.maciejszczurek;

import com.pivovarit.function.ThrowingSupplier;
import com.pivovarit.function.exception.WrappedException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URI;
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
import org.apache.commons.lang3.StringUtils;
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

  @TaskAction
  void downloadChromeDriver() throws IOException {
    final String os;
    if (SystemUtils.IS_OS_LINUX) {
      os = "linux64";
    } else if (SystemUtils.IS_OS_MAC) {
      os =
        "mac-%s".formatted(
            SystemUtils.OS_ARCH.equals("arm64") ? "arm64" : "x64"
          );
    } else {
      os = "win64";
    }

    final Path chromeDriverPath;
    final var tempFile = getTemporaryDir()
      .toPath()
      .resolve("chromedriver.zip")
      .toFile();
    FileUtils.copyURLToFile(
      URI
        .create(
          "https://edgedl.me.gvt1.com/edgedl/chrome/chrome-for-testing/%s/%s/chromedriver-%s.zip".formatted(
              getVersion().get(),
              os,
              os
            )
        )
        .toURL(),
      tempFile
    );

    try (var zipFile = new ZipFile(tempFile)) {
      final var zipEntry = zipFile
        .stream()
        .filter(zipEntry1 ->
          zipEntry1
            .getName()
            .startsWith("chromedriver", zipEntry1.getName().indexOf('/') + 1)
        )
        .findFirst()
        .orElseThrow();
      final var entryName = zipEntry.getName();
      chromeDriverPath =
        getProject()
          .getProjectDir()
          .toPath()
          .resolve(
            Path.of(
              "libs",
              "chromedriver",
              entryName.substring(entryName.indexOf('/') + 1)
            )
          );

      FileUtils.copyInputStreamToFile(
        zipFile.getInputStream(zipEntry),
        chromeDriverPath.toFile()
      );
    }

    Files.delete(tempFile.toPath());

    patchChrome(chromeDriverPath);
  }

  private void patchChrome(@NotNull Path chromeDriverPath) throws IOException {
    final var windowPattern = Pattern.compile("\\{window.*;}");
    final var chromeDriverSize = chromeDriverPath.toFile().length();
    final var chromeDriverFragment = chromeDriverSize >> 4;
    final var newLinesPositionsFuture =
      new ArrayList<CompletableFuture<List<lineFragment>>>();
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
      for (lineFragment lineFragment : newLinesPositions) {
        file.seek(lineFragment.index);

        var line = new byte[(int) lineFragment.size];
        file.read(line);

        final var matcher = windowPattern.matcher(
          new String(line, StandardCharsets.US_ASCII)
        );
        while (matcher.find()) {
          final var newWindowCodeBytes = StringUtils
            .leftPad(
              "{console.log('undetected chromedriver');}",
              matcher.end() - matcher.start()
            )
            .getBytes(StandardCharsets.US_ASCII);

          System.arraycopy(
            newWindowCodeBytes,
            0,
            line,
            matcher.start(),
            newWindowCodeBytes.length
          );
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
