package com.maciejszczurek.updatechecker.option.service;

import com.maciejszczurek.updatechecker.option.model.Option;
import com.maciejszczurek.updatechecker.option.model.OptionRepository;
import com.maciejszczurek.updatechecker.util.UpdateCheckerUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.type.TypeReference;

@RequiredArgsConstructor
@Service
@Transactional
public class OptionService {

  private final OptionRepository repository;

  @SuppressWarnings("UnusedReturnValue")
  public <T> Option setOption(final String name, final T value) {
    return repository.save(
      repository
        .findById(name)
        .orElse(new Option().setName(name))
        .setValue(
          UpdateCheckerUtils.getJsonMapper().writeValueAsString(value)
        )
    );
  }

  @Transactional(readOnly = true)
  public <T> T getOption(final String name, final T defaultValue) {
    final var option = repository.findById(name);

    return option.isPresent()
      ? UpdateCheckerUtils
        .getJsonMapper()
        .readValue(option.get().getValue(), new TypeReference<>() {})
      : defaultValue;
  }
}
