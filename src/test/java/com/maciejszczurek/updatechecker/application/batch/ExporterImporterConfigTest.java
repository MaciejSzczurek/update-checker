package com.maciejszczurek.updatechecker.application.batch;

import static org.assertj.core.api.Assertions.assertThat;

import com.maciejszczurek.updatechecker.application.model.Application;
import com.maciejszczurek.updatechecker.application.model.ApplicationType;
import com.maciejszczurek.updatechecker.application.repository.ApplicationRepository;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@AutoConfigureTestDatabase
@Rollback
@ActiveProfiles("test")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExporterImporterConfigTest {

  private JobLauncherTestUtils jobLauncherTestUtils;

  @Autowired
  private JobRepository jobRepository;

  @Autowired
  private JobLauncher jobLauncher;

  @Autowired
  private Job exportApplications;

  @Autowired
  private Job importApplications;

  @Autowired
  private ApplicationRepository applicationRepository;

  @BeforeEach
  public void setUp() {
    jobLauncherTestUtils = new JobLauncherTestUtils();
    jobLauncherTestUtils.setJobLauncher(jobLauncher);
    jobLauncherTestUtils.setJobRepository(jobRepository);
    jobLauncherTestUtils.setJob(exportApplications);

    applicationRepository.save(
      new Application()
        .setName("EnumTest 1")
        .setApplicationType(ApplicationType.NSANE_DOWN)
        .setSiteUrl("http://test-1.pl")
        .setCurrentVersion("1.1")
    );
    applicationRepository.save(
      new Application()
        .setName("EnumTest 2")
        .setApplicationType(ApplicationType.DOBREPROGRAMY)
        .setSiteUrl("http://test-2.pl")
        .setCurrentVersion("2.2")
    );
  }

  @Test
  public void exportApplications() throws Exception {
    final JobParameters jobParameter = new JobParametersBuilder()
      .addString("file", "./applications.txt")
      .toJobParameters();

    jobLauncherTestUtils.launchJob(
      new JobParametersBuilder()
        .addString("file", "./applications.txt")
        .toJobParameters()
    );

    applicationRepository.deleteAll();

    assertThat(applicationRepository.findAll()).isEmpty();

    jobLauncherTestUtils.setJob(importApplications);
    jobLauncherTestUtils.launchJob(jobParameter);

    final List<Application> applicationList = applicationRepository.findAll();

    assertThat(applicationList).hasSize(2);
    assertThat(applicationList.stream().map(Application::getName))
      .contains("EnumTest 1", "EnumTest 2");
  }
}
