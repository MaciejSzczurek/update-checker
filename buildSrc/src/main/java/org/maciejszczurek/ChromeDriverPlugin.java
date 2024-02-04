package org.maciejszczurek;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.BasePlugin;
import org.jetbrains.annotations.NotNull;

public class ChromeDriverPlugin implements Plugin<Project> {

  @Override
  public void apply(@NotNull final Project project) {
    project
      .getTasks()
      .getByName("classes")
      .dependsOn(
        project
          .getTasks()
          .create(
            "downloadChromeDriver",
            DownloadChromeDriverTask.class,
            configuration -> {
              configuration.setDescription(
                "Download and make a patch for the chrome driver executable."
              );
              configuration.setGroup(BasePlugin.BUILD_GROUP);
            }
          )
      );
  }
}
