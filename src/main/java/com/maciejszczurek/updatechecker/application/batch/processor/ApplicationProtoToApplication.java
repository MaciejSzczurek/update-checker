package com.maciejszczurek.updatechecker.application.batch.processor;

import com.maciejszczurek.updatechecker.application.model.Application;
import com.maciejszczurek.updatechecker.application.model.ApplicationType;
import com.maciejszczurek.updatechecker.application.proto.ApplicationProto;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.jetbrains.annotations.NotNull;
import org.springframework.batch.infrastructure.item.ItemProcessor;

public class ApplicationProtoToApplication
  implements ItemProcessor<ApplicationProto.Application, Application> {

  @Override
  public Application process(
    @NotNull final ApplicationProto.Application applicationProto
  ) {
    return new Application()
      .setName(applicationProto.getName())
      .setApplicationType(
        ApplicationType.valueOf(applicationProto.getApplicationType())
      )
      .setSiteUrl(applicationProto.getSiteUrl())
      .setUpdateUrl(
        !applicationProto.getUpdateUrl().equals("")
          ? applicationProto.getUpdateUrl()
          : null
      )
      .setCurrentVersion(applicationProto.getCurrentVersion())
      .setLastUpdate(
        applicationProto.getLastUpdate() != 0L
          ? LocalDateTime.ofEpochSecond(
            applicationProto.getLastUpdate(),
            0,
            ZoneOffset.UTC
          )
          : null
      )
      .setIgnored(applicationProto.getIgnored());
  }
}
