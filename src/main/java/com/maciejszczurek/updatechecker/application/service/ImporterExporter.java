package com.maciejszczurek.updatechecker.application.service;

import java.io.File;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.InvalidJobParametersException;
import org.springframework.batch.core.job.parameters.JobParameter;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.launch.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobRestartException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ImporterExporter {

  private final JobLauncher jobLauncher;
  private final Job importApplications;
  private final Job exportApplications;

  public JobExecution importApplications(final File file)
    throws InvalidJobParametersException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
    return jobLauncher.run(
      importApplications,
      new JobParametersBuilder()
        .addDate("date", new Date())
        .addJobParameter(new JobParameter<>("file", file, File.class, false))
        .toJobParameters()
    );
  }

  public JobExecution exportApplications(final File file)
    throws InvalidJobParametersException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
    return jobLauncher.run(
      exportApplications,
      new JobParametersBuilder()
        .addDate("date", new Date())
        .addJobParameter(new JobParameter<>("file", file, File.class, false))
        .toJobParameters()
    );
  }
}
