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

class ExtensionConfigurationFunctionalTest {

  @TempDir
  File projectDir;

  @Test
  void customAvroSchemaSrcIsUsed() throws IOException {
    String buildFileContent =
        """
            plugins {
                id 'java'
                id 'kaa.rfcs-gradle-plugin'
            }
            kaaAvro {
                avroSchemaSrc = layout.buildDirectory.dir('custom-schemas')
            }
            repositories { mavenCentral() }
            """;

    Files.writeString(projectDir.toPath().resolve("build.gradle"), buildFileContent);
    Files.writeString(projectDir.toPath().resolve("settings.gradle"), "rootProject.name = 'kaa-test'");

    GradleRunner.create()
        .forwardOutput()
        .withPluginClasspath()
        .withProjectDir(projectDir)
        .withArguments("extractKaaSchemas")
        .build();

    File customDir = new File(projectDir, "build/custom-schemas");
    assertThat(customDir.exists()).isTrue();
  }

  @Test
  void customGeneratedSrcIsUsed() throws IOException {
    String buildFileContent =
        """
            plugins {
                id 'java'
                id 'kaa.rfcs-gradle-plugin'
            }
            kaaAvro {
                generatedSrc = layout.buildDirectory.dir('custom-generated')
            }
            repositories { mavenCentral() }
            """;

    Files.writeString(projectDir.toPath().resolve("build.gradle"), buildFileContent);
    Files.writeString(projectDir.toPath().resolve("settings.gradle"), "rootProject.name = 'kaa-test'");

    GradleRunner.create()
        .forwardOutput()
        .withPluginClasspath()
        .withProjectDir(projectDir)
        .withArguments("generateAvroJava")
        .build();

    File customDir = new File(projectDir, "build/custom-generated");
    assertThat(customDir.exists()).isTrue();
  }

  @Test
  void customDirectoriesAreCleaned() throws IOException {
    String buildFileContent =
        """
            plugins {
                id 'java'
                id 'kaa.rfcs-gradle-plugin'
            }
            kaaAvro {
                avroSchemaSrc = layout.buildDirectory.dir('custom-schemas')
                generatedSrc = layout.buildDirectory.dir('custom-generated')
            }
            repositories { mavenCentral() }
            """;

    Files.writeString(projectDir.toPath().resolve("build.gradle"), buildFileContent);
    Files.writeString(projectDir.toPath().resolve("settings.gradle"), "rootProject.name = 'kaa-test'");

    // Create directories
    GradleRunner.create()
        .forwardOutput()
        .withPluginClasspath()
        .withProjectDir(projectDir)
        .withArguments("extractKaaSchemas")
        .build();

    File schemasDir = new File(projectDir, "build/custom-schemas");
    assertThat(schemasDir.exists()).isTrue();

    // Clean
    GradleRunner.create()
        .forwardOutput()
        .withPluginClasspath()
        .withProjectDir(projectDir)
        .withArguments("clean")
        .build();

    assertThat(schemasDir.exists()).isFalse();
  }

  @Test
  void configurationViaKaaAvroBlockWorks() throws IOException {
    String buildFileContent =
        """
            plugins {
                id 'java'
                id 'kaa.rfcs-gradle-plugin'
            }
            repositories { mavenCentral() }
            kaaAvro {
                avroSchemaSrc = layout.buildDirectory.dir('test-schemas')
            }
            """;

    Files.writeString(projectDir.toPath().resolve("build.gradle"), buildFileContent);
    Files.writeString(projectDir.toPath().resolve("settings.gradle"), "rootProject.name = 'kaa-test'");

    BuildResult result =
        GradleRunner.create()
            .forwardOutput()
            .withPluginClasspath()
            .withProjectDir(projectDir)
            .withArguments("extractKaaSchemas")
            .build();

    assertThat(result.task(":extractKaaSchemas").getOutcome()).isEqualTo(TaskOutcome.SUCCESS);
  }

  @Test
  void defaultValuesUsedWhenNotConfigured() throws IOException {
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

    GradleRunner.create()
        .forwardOutput()
        .withPluginClasspath()
        .withProjectDir(projectDir)
        .withArguments("extractKaaSchemas")
        .build();

    File defaultDir = new File(projectDir, "build/kaa-schemas");
    assertThat(defaultDir.exists()).isTrue();
  }

  @Test
  void extensionIsAccessibleInBuildScript() throws IOException {
    String buildFileContent =
        """
            plugins {
                id 'java'
                id 'kaa.rfcs-gradle-plugin'
            }
            repositories { mavenCentral() }
            tasks.register('verifyExtension') {
                doLast {
                    assert project.kaaAvro != null
                    assert project.kaaAvro.avroSchemaSrc != null
                    assert project.kaaAvro.generatedSrc != null
                }
            }
            """;

    Files.writeString(projectDir.toPath().resolve("build.gradle"), buildFileContent);
    Files.writeString(projectDir.toPath().resolve("settings.gradle"), "rootProject.name = 'kaa-test'");

    BuildResult result =
        GradleRunner.create()
            .forwardOutput()
            .withPluginClasspath()
            .withProjectDir(projectDir)
            .withArguments("verifyExtension")
            .build();

    assertThat(result.task(":verifyExtension").getOutcome()).isEqualTo(TaskOutcome.SUCCESS);
  }
}
