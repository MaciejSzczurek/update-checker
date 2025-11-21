package com.maciejszczurek.updatechecker.application.batch;

import com.maciejszczurek.updatechecker.application.batch.processor.ApplicationProtoToApplication;
import com.maciejszczurek.updatechecker.application.batch.reader.ApplicationFileReader;
import com.maciejszczurek.updatechecker.application.model.Application;
import com.maciejszczurek.updatechecker.application.proto.ApplicationProto;
import com.maciejszczurek.updatechecker.application.repository.ApplicationRepository;
import jakarta.persistence.EntityManagerFactory;
import java.io.File;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.database.JpaItemWriter;
import org.springframework.batch.infrastructure.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.batch.infrastructure.repeat.policy.DefaultResultCompletionPolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class ImporterConfig extends DefaultBatchConfiguration {

  private final JobRepository jobRepository;
  private final PlatformTransactionManager transactionManager;
  private final EntityManagerFactory entityManagerFactory;

  @Bean
  public Job importApplications(
    final Step removeApplicationsStep,
    final Step importApplicationsStep
  ) {
    return new JobBuilder("importApplications", jobRepository)
      .start(removeApplicationsStep)
      .next(importApplicationsStep)
      .build();
  }

  @Bean
  public Step removeApplicationsStep(final ApplicationRepository repository) {
    return new StepBuilder("removeApplicationsStep", jobRepository)
      .tasklet(
        (contribution, chunkContext) -> {
          repository.deleteAll();
          return RepeatStatus.FINISHED;
        },
        transactionManager
      )
      .build();
  }

  @Bean
  public Step importApplicationsStep(
    final ApplicationFileReader applicationFileReader
  ) {
    return new StepBuilder("importApplicationsStep", jobRepository)
      .<ApplicationProto.Application, Application>chunk(
        new DefaultResultCompletionPolicy(),
        transactionManager
      )
      .reader(applicationFileReader)
      .processor(applicationProtoToApplication())
      .writer(applicationJpaItemWriter())
      .build();
  }

  @Bean
  @JobScope
  public ApplicationFileReader applicationFileReader(
    @NotNull @Value("#{jobParameters['file']}") final File file
  ) {
    return new ApplicationFileReader(file.toPath());
  }

  @Bean
  public ApplicationProtoToApplication applicationProtoToApplication() {
    return new ApplicationProtoToApplication();
  }

  @Bean
  public JpaItemWriter<Application> applicationJpaItemWriter() {
    return new JpaItemWriterBuilder<Application>()
      .entityManagerFactory(entityManagerFactory)
      .build();
  }
}
