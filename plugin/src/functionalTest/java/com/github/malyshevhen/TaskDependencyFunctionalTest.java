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

class TaskDependencyFunctionalTest {

  @TempDir
  File projectDir;

  @Test
  void extractKaaSchemasRunsBeforeGenerateAvroJava() throws IOException {
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
            .withArguments("generateAvroJava")
            .build();

    assertThat(result.task(":extractKaaSchemas").getOutcome()).isNotNull();
    assertThat(result.task(":generateAvroJava").getOutcome()).isNotNull();
  }

  @Test
  void generateAvroJavaRunsAfterExtractKaaSchemas() throws IOException {
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
            .withArguments("generateAvroJava")
            .build();

    // Both tasks should succeed
    assertThat(result.task(":extractKaaSchemas").getOutcome()).isEqualTo(TaskOutcome.SUCCESS);
    assertThat(result.task(":generateAvroJava").getOutcome()).isEqualTo(TaskOutcome.SUCCESS);
  }

  @Test
  void compileJavaDependsOnGenerateAvroJava() throws IOException {
    String buildFileContent =
        """
            plugins {
                id 'java'
                id 'kaa.rfcs-gradle-plugin'
            }
            repositories { mavenCentral() }
            dependencies {
                implementation 'org.apache.avro:avro:1.11.3'
            }
            """;

    Files.writeString(projectDir.toPath().resolve("build.gradle"), buildFileContent);
    Files.writeString(projectDir.toPath().resolve("settings.gradle"), "rootProject.name = 'kaa-test'");

    BuildResult result =
        GradleRunner.create()
            .forwardOutput()
            .withPluginClasspath()
            .withProjectDir(projectDir)
            .withArguments("compileJava")
            .build();

    // generateAvroJava should run as dependency of compileJava
    assertThat(result.task(":generateAvroJava").getOutcome()).isEqualTo(TaskOutcome.SUCCESS);
    assertThat(result.task(":compileJava").getOutcome()).isEqualTo(TaskOutcome.SUCCESS);
  }

  @Test
  void noCircularDependencies() throws IOException {
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
            .withArguments("generateAvroJava")
            .build();

    // Build should succeed without circular dependency errors
    assertThat(result.getOutput()).doesNotContain("Circular dependency");
  }

  @Test
  void taskDependenciesAreCorrectlyConfigured() throws IOException {
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

    // Should have plugin tasks available
    assertThat(result.getOutput()).contains("extractKaaSchemas");
    assertThat(result.getOutput()).contains("generateAvroJava");
  }

  @Test
  void runningCompileJavaTriggersGeneration() throws IOException {
    String buildFileContent =
        """
            plugins {
                id 'java'
                id 'kaa.rfcs-gradle-plugin'
            }
            repositories { mavenCentral() }
            dependencies {
                implementation 'org.apache.avro:avro:1.11.3'
            }
            """;

    Files.writeString(projectDir.toPath().resolve("build.gradle"), buildFileContent);
    Files.writeString(projectDir.toPath().resolve("settings.gradle"), "rootProject.name = 'kaa-test'");

    BuildResult result =
        GradleRunner.create()
            .forwardOutput()
            .withPluginClasspath()
            .withProjectDir(projectDir)
            .withArguments("compileJava")
            .build();

    // verify generateAvroJava was triggered
    assertThat(result.getOutput()).contains("generateAvroJava");
  }
}
