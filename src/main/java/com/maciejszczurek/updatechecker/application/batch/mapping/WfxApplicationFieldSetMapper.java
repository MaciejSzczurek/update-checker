package com.maciejszczurek.updatechecker.application.batch.mapping;

import com.maciejszczurek.updatechecker.application.model.Application;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;

@NoArgsConstructor
public class WfxApplicationFieldSetMapper
  implements FieldSetMapper<Application> {

  @Override
  @NotNull
  public Application mapFieldSet(@NotNull final FieldSet fieldSet) {
    if (fieldSet.getFieldCount() == 6) {
      return new Application()
        .setName(fieldSet.readString(0))
        .setSiteUrl(fieldSet.readString(1))
        .setCurrentVersion(fieldSet.readString(5));
    }

    return new Application()
      .setName(fieldSet.readString(0))
      .setUpdateUrl(fieldSet.readString(2))
      .setSiteUrl(fieldSet.readString(3))
      .setCurrentVersion(fieldSet.readString(7));
  }
}
