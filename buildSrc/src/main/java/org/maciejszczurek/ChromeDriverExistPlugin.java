package org.maciejszczurek;

import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.commons.lang3.SystemUtils;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.jetbrains.annotations.NotNull;

public class ChromeDriverExistPlugin implements Plugin<Project> {

  @Override
  public void apply(@NotNull final Project project) {
    project
      .getTasks()
      .stream()
      .filter(task -> task.getName().equals("bootJar"))
      .findFirst()
      .ifPresent(bootJarTask ->
        bootJarTask.doFirst(task -> {
          if (
            !Files.exists(
              task
                .getProject()
                .getProjectDir()
                .toPath()
                .resolve(
                  Path.of(
                    "build",
                    "resources",
                    "main",
                    "chromedriver%s".formatted(
                        SystemUtils.IS_OS_WINDOWS ? ".exe" : ""
                      )
                  )
                )
            )
          ) {
            throw new ChromeDriverNotExists();
          }
        })
      );
  }
}
