package com.maciejszczurek.updatechecker.application.batch.processor;

import com.maciejszczurek.updatechecker.application.model.Application;
import com.maciejszczurek.updatechecker.application.proto.ApplicationProto;
import java.time.ZoneOffset;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.springframework.batch.item.ItemProcessor;

public class ApplicationToProtoProcessor
  implements ItemProcessor<Application, ApplicationProto.Application> {

  @Override
  public ApplicationProto.Application process(
    @NotNull final Application application
  ) {
    final var applicationProtoBuilder = ApplicationProto.Application
      .newBuilder()
      .setName(application.getName())
      .setApplicationType(application.getApplicationType().name())
      .setSiteUrl(application.getSiteUrl())
      .setUpdateUrl(Optional.ofNullable(application.getUpdateUrl()).orElse(""))
      .setCurrentVersion(application.getCurrentVersion())
      .setIgnored(application.isIgnored());

    Optional
      .ofNullable(application.getLastUpdate())
      .map(lastUpdate -> lastUpdate.toEpochSecond(ZoneOffset.UTC))
      .ifPresent(applicationProtoBuilder::setLastUpdate);

    return applicationProtoBuilder.build();
  }
}
