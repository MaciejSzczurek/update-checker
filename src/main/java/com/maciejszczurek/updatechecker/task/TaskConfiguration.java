package com.maciejszczurek.updatechecker.task;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.task.ThreadPoolTaskExecutorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@RequiredArgsConstructor
public class TaskConfiguration {

  @Value("${spring.task.execution.pool.core-size:}")
  private Integer coreSize;

  @Bean
  public ThreadPoolTaskExecutor applicationTaskExecutor(
    @NotNull final ThreadPoolTaskExecutorBuilder builder
  ) {
    return builder
      .corePoolSize(
        Optional
          .ofNullable(coreSize)
          .orElse(Runtime.getRuntime().availableProcessors() * 4)
      )
      .build();
  }
}
