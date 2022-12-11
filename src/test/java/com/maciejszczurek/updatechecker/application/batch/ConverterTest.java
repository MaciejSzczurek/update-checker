package com.maciejszczurek.updatechecker.application.batch;

import static org.assertj.core.api.Assertions.assertThat;

import com.maciejszczurek.updatechecker.application.repository.ApplicationRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class ConverterTest {

  @Autowired
  private JobRepository jobRepository;

  @Autowired
  private JobLauncher jobLauncher;

  @Autowired
  private Job wfxVersionConverter;

  @Autowired
  private ApplicationRepository applicationRepository;

  private JobLauncherTestUtils jobLauncherTestUtils;

  @BeforeEach
  public void setUp() {
    jobLauncherTestUtils = new JobLauncherTestUtils();
    jobLauncherTestUtils.setJobLauncher(jobLauncher);
    jobLauncherTestUtils.setJobRepository(jobRepository);
    jobLauncherTestUtils.setJob(wfxVersionConverter);

    applicationRepository.deleteAll();
  }

  @Test
  public void jobPrograms() throws Exception {
    final JobExecution jobExecution = jobLauncherTestUtils.launchJob(
      new JobParametersBuilder()
        .addString(
          "filename",
          "c:\\Users\\Maciej\\AppData\\Roaming\\Waterfox\\Profiles\\mxz609dh.default\\Versions\\Programy.list"
        )
        .addString(
          "time",
          LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
        )
        .toJobParameters()
    );

    assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
  }

  @Test
  public void jobCMS() throws Exception {
    final JobExecution jobExecution = jobLauncherTestUtils.launchJob(
      new JobParametersBuilder()
        .addString(
          "filename",
          "c:\\Users\\Maciej\\AppData\\Roaming\\Waterfox\\Profiles\\mxz609dh.default\\Versions\\CMS.list"
        )
        .addString(
          "time",
          LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
        )
        .toJobParameters()
    );

    assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
  }

  @Test
  public void jobDrivers() throws Exception {
    final JobExecution jobExecution = jobLauncherTestUtils.launchJob(
      new JobParametersBuilder()
        .addString(
          "filename",
          "c:\\Users\\Maciej\\AppData\\Roaming\\Waterfox\\Profiles\\mxz609dh.default\\Versions\\Sterowniki.list"
        )
        .addString(
          "time",
          LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
        )
        .toJobParameters()
    );

    assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
  }
}
