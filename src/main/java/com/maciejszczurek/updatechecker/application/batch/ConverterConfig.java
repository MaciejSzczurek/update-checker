package com.maciejszczurek.updatechecker.application.batch;

import com.maciejszczurek.updatechecker.application.batch.mapping.WfxApplicationFieldSetMapper;
import com.maciejszczurek.updatechecker.application.batch.processor.ApplicationItemProcessor;
import com.maciejszczurek.updatechecker.application.model.Application;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.repeat.policy.DefaultResultCompletionPolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class ConverterConfig extends DefaultBatchConfiguration {

  private final EntityManagerFactory entityManager;

  @Bean
  public Job wfxVersionConverter(
    final JobRepository jobRepository,
    final Step readWfxVersionCsv
  ) {
    return new JobBuilder("wfxVersionConverter", jobRepository)
      .start(readWfxVersionCsv)
      .build();
  }

  @Bean
  public Step readWfxVersionCsv(
    final JobRepository jobRepository,
    final FlatFileItemReader<Application> csvFileReader,
    final PlatformTransactionManager transactionManager
  ) {
    return new StepBuilder("readWfxVersionCsv", jobRepository)
      .<Application, Application>chunk(
        new DefaultResultCompletionPolicy(),
        transactionManager
      )
      .reader(csvFileReader)
      .processor(new ApplicationItemProcessor())
      .writer(jpaItemWriter())
      .build();
  }

  @Bean
  public JpaItemWriter<Application> jpaItemWriter() {
    final JpaItemWriter<Application> itemWriter = new JpaItemWriter<>();
    itemWriter.setEntityManagerFactory(entityManager);

    return itemWriter;
  }

  @Bean
  @StepScope
  public FlatFileItemReader<Application> csvFileReader(
    @Value("file:#{jobParameters['filename']}") final Resource resource
  ) {
    return new FlatFileItemReaderBuilder<Application>()
      .lineMapper(lineMapper())
      .name("csvFileReader")
      .resource(resource)
      .build();
  }

  @Bean
  public DefaultLineMapper<Application> lineMapper() {
    final DefaultLineMapper<Application> lineMapper = new DefaultLineMapper<>();
    lineMapper.setLineTokenizer(lineTokenizer());
    lineMapper.setFieldSetMapper(new WfxApplicationFieldSetMapper());

    return lineMapper;
  }

  @Bean
  public DelimitedLineTokenizer lineTokenizer() {
    final var lineTokenizer = new DelimitedLineTokenizer("|");
    lineTokenizer.setQuoteCharacter('~');

    return lineTokenizer;
  }
}
