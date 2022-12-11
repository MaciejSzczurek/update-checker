package com.maciejszczurek.updatechecker.application.batch.reader;

import com.maciejszczurek.updatechecker.application.proto.ApplicationProto;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import org.jetbrains.annotations.NotNull;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.support.AbstractItemStreamItemReader;

public class ApplicationFileReader
  extends AbstractItemStreamItemReader<ApplicationProto.Application> {

  private final Path path;
  private InputStream inputStream;

  public ApplicationFileReader(final Path path) {
    this.path = path;

    setName("ApplicationFileReader");
  }

  @Override
  public ApplicationProto.Application read() {
    try {
      return ApplicationProto.Application.parseDelimitedFrom(inputStream);
    } catch (IOException e) {
      throw new ItemStreamException("Error while reading from file.", e);
    }
  }

  @Override
  public void open(@NotNull final ExecutionContext executionContext) {
    try {
      inputStream = new BufferedInputStream(Files.newInputStream(path));
    } catch (IOException e) {
      throw new ItemStreamException("Error while opening file.", e);
    }
  }

  @Override
  public void close() {
    if (inputStream != null) {
      try {
        inputStream.close();
      } catch (IOException e) {
        throw new ItemStreamException("Error while closing file.", e);
      }
    }
  }
}
