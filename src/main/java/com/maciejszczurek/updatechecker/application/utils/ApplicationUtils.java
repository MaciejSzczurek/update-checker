package com.maciejszczurek.updatechecker.application.utils;

import com.maciejszczurek.updatechecker.application.model.Application;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class ApplicationUtils {

  public String getUpdateUrl(@NotNull final Application application) {
    return (
        application.getUpdateUrl() == null ||
        application.getUpdateUrl().isEmpty()
      )
      ? application.getSiteUrl()
      : application.getUpdateUrl();
  }
}
