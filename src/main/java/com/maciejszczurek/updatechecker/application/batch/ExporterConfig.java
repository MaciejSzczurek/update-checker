package com.maciejszczurek.updatechecker.application.batch;

import com.maciejszczurek.updatechecker.application.batch.processor.ApplicationToProtoProcessor;
import com.maciejszczurek.updatechecker.application.batch.writer.ApplicationFileWriter;
import com.maciejszczurek.updatechecker.application.model.Application;
import com.maciejszczurek.updatechecker.application.proto.ApplicationProto;
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
import org.springframework.batch.infrastructure.item.database.JpaPagingItemReader;
import org.springframework.batch.infrastructure.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.infrastructure.repeat.policy.DefaultResultCompletionPolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class ExporterConfig extends DefaultBatchConfiguration {

  private final EntityManagerFactory entityManagerFactory;

  @Bean
  public Job exportApplications(
    final Step exportApplicationsStep,
    final JobRepository jobRepository
  ) {
    return new JobBuilder("exportApplications", jobRepository)
      .start(exportApplicationsStep)
      .build();
  }

  @Bean
  public Step exportApplicationsStep(
    final ApplicationFileWriter applicationFileWriter,
    final PlatformTransactionManager transactionManager,
    final JobRepository jobRepository
  ) {
    return new StepBuilder("exportApplicationsStep", jobRepository)
      .<Application, ApplicationProto.Application>chunk(
        new DefaultResultCompletionPolicy(),
        transactionManager
      )
      .reader(applicationJpaPagingItemReader())
      .processor(applicationToProtoProcessor())
      .writer(applicationFileWriter)
      .build();
  }

  @Bean
  public ApplicationToProtoProcessor applicationToProtoProcessor() {
    return new ApplicationToProtoProcessor();
  }

  @Bean(destroyMethod = "")
  public JpaPagingItemReader<Application> applicationJpaPagingItemReader() {
    return new JpaPagingItemReaderBuilder<Application>()
      .name("applicationJpaPagingItemReader")
      .entityManagerFactory(entityManagerFactory)
      .queryString("select a from Application as a order by a.name asc")
      .build();
  }

  @Bean
  @JobScope
  public ApplicationFileWriter applicationFileWriter(
    @NotNull @Value("#{jobParameters['file']}") final File file
  ) {
    return new ApplicationFileWriter(file.toPath());
  }
}
