package com.maciejszczurek.updatechecker.application.batch.writer;

import com.maciejszczurek.updatechecker.application.proto.ApplicationProto;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import org.jetbrains.annotations.NotNull;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.support.AbstractItemStreamItemWriter;

public class ApplicationFileWriter
  extends AbstractItemStreamItemWriter<ApplicationProto.Application> {

  private final Path path;
  private OutputStream outputStream;

  public ApplicationFileWriter(final Path path) {
    this.path = path;

    setName("ApplicationFileWriter");
  }

  @Override
  public void close() {
    if (outputStream != null) {
      try {
        outputStream.close();
      } catch (IOException e) {
        throw new ItemStreamException("Error while closing file.", e);
      }
    }
  }

  @Override
  public void open(@NotNull final ExecutionContext executionContext) {
    try {
      outputStream = new BufferedOutputStream(Files.newOutputStream(path));
    } catch (IOException e) {
      throw new ItemStreamException("Error while opening file.", e);
    }
  }

  @Override
  public void write(
    @NotNull final Chunk<? extends ApplicationProto.Application> items
  ) {
    items.forEach(application -> {
      try {
        application.writeDelimitedTo(outputStream);
      } catch (IOException e) {
        throw new ItemStreamException("Error while writing to file.", e);
      }
    });
  }
}
