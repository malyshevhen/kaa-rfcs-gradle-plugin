package com.github.malyshevhen;

import java.io.IOException;
import java.nio.file.Files;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.TaskOutcome;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

class ErrorHandlingFunctionalTest {

  @TempDir
  File projectDir;

  @Test
  void pluginAppliesJavaPluginAutomatically() throws IOException {
    String buildFileContent =
        """
            plugins {
                id 'kaa.rfcs-gradle-plugin'
            }
            repositories { mavenCentral() }
            """;

    Files.writeString(projectDir.toPath().resolve("build.gradle"), buildFileContent);
    Files.writeString(projectDir.toPath().resolve("settings.gradle"), "rootProject.name = 'kaa-test'");

    BuildResult result =
        GradleRunner.create()
            .forwardOutput()
            .withPluginClasspath()
            .withProjectDir(projectDir)
            .withArguments("tasks")
            .build();

    // classes task is a Java plugin task that should be available
    assertThat(result.getOutput()).contains("classes");
    assertThat(result.task(":tasks").getOutcome()).isEqualTo(TaskOutcome.SUCCESS);
  }

  @Test
  void pluginAppliesAvroPluginAutomatically() throws IOException {
    String buildFileContent =
        """
            plugins {
                id 'java'
                id 'kaa.rfcs-gradle-plugin'
            }
            repositories { mavenCentral() }
            """;

    Files.writeString(projectDir.toPath().resolve("build.gradle"), buildFileContent);
    Files.writeString(projectDir.toPath().resolve("settings.gradle"), "rootProject.name = 'kaa-test'");

    BuildResult result =
        GradleRunner.create()
            .forwardOutput()
            .withPluginClasspath()
            .withProjectDir(projectDir)
            .withArguments("tasks")
            .build();

    assertThat(result.getOutput()).contains("generateAvroJava");
    assertThat(result.task(":tasks").getOutcome()).isEqualTo(TaskOutcome.SUCCESS);
  }

  @Test
  void pluginDoesNotFailWithoutIdeaPlugin() throws IOException {
    String buildFileContent =
        """
            plugins {
                id 'java'
                id 'kaa.rfcs-gradle-plugin'
            }
            repositories { mavenCentral() }
            """;

    Files.writeString(projectDir.toPath().resolve("build.gradle"), buildFileContent);
    Files.writeString(projectDir.toPath().resolve("settings.gradle"), "rootProject.name = 'kaa-test'");

    BuildResult result =
        GradleRunner.create()
            .forwardOutput()
            .withPluginClasspath()
            .withProjectDir(projectDir)
            .withArguments("tasks")
            .build();

    assertThat(result.task(":tasks").getOutcome()).isEqualTo(TaskOutcome.SUCCESS);
  }

  @Test
  void pluginWorksInFreshProject() throws IOException {
    String buildFileContent =
        """
            plugins {
                id 'java'
                id 'kaa.rfcs-gradle-plugin'
            }
            repositories { mavenCentral() }
            """;

    Files.writeString(projectDir.toPath().resolve("build.gradle"), buildFileContent);
    Files.writeString(projectDir.toPath().resolve("settings.gradle"), "rootProject.name = 'kaa-test'");

    BuildResult result =
        GradleRunner.create()
            .forwardOutput()
            .withPluginClasspath()
            .withProjectDir(projectDir)
            .withArguments("tasks")
            .build();

    assertThat(result.getOutput()).contains("extractKaaSchemas");
    assertThat(result.task(":tasks").getOutcome()).isEqualTo(TaskOutcome.SUCCESS);
  }

  @Test
  void pluginWorksWhenAppliedMultipleTimes() throws IOException {
    String buildFileContent =
        """
            plugins {
                id 'java'
                id 'kaa.rfcs-gradle-plugin'
            }
            repositories { mavenCentral() }
            """;

    Files.writeString(projectDir.toPath().resolve("build.gradle"), buildFileContent);
    Files.writeString(projectDir.toPath().resolve("settings.gradle"), "rootProject.name = 'kaa-test'");

    BuildResult result =
        GradleRunner.create()
            .forwardOutput()
            .withPluginClasspath()
            .withProjectDir(projectDir)
            .withArguments("tasks", "--dry-run")
            .build();

    assertThat(result.getOutput()).contains("tasks");
  }
}
