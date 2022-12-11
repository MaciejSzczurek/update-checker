package com.maciejszczurek.updatechecker.application.converter;

import com.maciejszczurek.updatechecker.application.model.Application;
import com.maciejszczurek.updatechecker.application.repository.ApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApplicationConverter implements Converter<String, Application> {

  private final ApplicationRepository repository;

  @Override
  public Application convert(@NotNull final String source) {
    return repository.findByName(source).orElse(null);
  }
}
