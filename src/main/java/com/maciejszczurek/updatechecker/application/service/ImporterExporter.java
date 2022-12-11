package com.maciejszczurek.updatechecker.application.service;

import java.io.File;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ImporterExporter {

  private final JobLauncher jobLauncher;
  private final Job importApplications;
  private final Job exportApplications;

  public JobExecution importApplications(final File file)
    throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
    return jobLauncher.run(
      importApplications,
      new JobParametersBuilder()
        .addDate("date", new Date())
        .addJobParameter("file", new JobParameter<>(file, File.class, false))
        .toJobParameters()
    );
  }

  public JobExecution exportApplications(final File file)
    throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
    return jobLauncher.run(
      exportApplications,
      new JobParametersBuilder()
        .addDate("date", new Date())
        .addJobParameter("file", new JobParameter<>(file, File.class, false))
        .toJobParameters()
    );
  }
}
