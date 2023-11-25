package org.maciejszczurek;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.jetbrains.annotations.NotNull;

public class ChromeDriverPlugin implements Plugin<Project> {

  @Override
  public void apply(@NotNull final Project project) {
    project
      .getTasks()
      .getByName("processResources")
      .dependsOn(
        project
          .getTasks()
          .create("downloadChromeDriver", DownloadChromeDriverTask.class)
      );
  }
}
