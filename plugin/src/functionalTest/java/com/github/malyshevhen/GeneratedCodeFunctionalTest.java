package com.github.malyshevhen;

import java.io.IOException;
import java.nio.file.Files;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.TaskOutcome;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class GeneratedCodeFunctionalTest {

  @TempDir
  File projectDir;

  @Test
  void generatedJavaCodeCompilesWithoutErrors() throws IOException {
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

    assertThat(result.task(":generateAvroJava").getOutcome()).isEqualTo(TaskOutcome.SUCCESS);
    assertThat(result.task(":compileJava").getOutcome()).isEqualTo(TaskOutcome.SUCCESS);
  }

  @Test
  void allSchemaFilesAreProcessed() throws IOException {
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
        .withArguments("generateAvroJava")
        .build();

    File generatedDir = new File(projectDir, "build/generated/sources/avro/java/main");
    long javaFileCount =
        Files.walk(generatedDir.toPath()).filter(p -> p.toString().endsWith(".java")).count();

    assertThat(javaFileCount).isGreaterThan(10);
  }

  @Test
  void generatedClassesHaveCorrectPackageStructure() throws IOException {
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
        .withArguments("generateAvroJava")
        .build();

    // Check that generated files have proper package structure
    Path generatedDir = projectDir.toPath().resolve("build/generated/sources/avro/java/main");
    boolean hasPackageStructure =
        Files.walk(generatedDir).anyMatch(p -> p.toString().contains("org/kaaproject"));

    assertThat(hasPackageStructure).isTrue();
  }

  @Test
  void schemaDependenciesAreResolved() throws IOException {
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
        .withArguments("generateAvroJava")
        .build();

    File generatedDir = new File(projectDir, "build/generated/sources/avro/java/main");
    assertThat(generatedDir.exists()).isTrue();

    // If complex schemas with dependencies exist, they should all compile
    long javaFileCount =
        Files.walk(generatedDir.toPath()).filter(p -> p.toString().endsWith(".java")).count();
    assertThat(javaFileCount).isGreaterThan(0);
  }

  @Test
  void generatedClassesImplementSpecificRecord() throws IOException {
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

    GradleRunner.create()
        .forwardOutput()
        .withPluginClasspath()
        .withProjectDir(projectDir)
        .withArguments("generateAvroJava")
        .build();

    Path generatedDir =
        projectDir.toPath().resolve("build/generated/sources/avro/java/main");
    Path clientDataPath = generatedDir.resolve("org/kaaproject/kaa/common/channels/ClientData.java");

    assertThat(Files.exists(clientDataPath)).isTrue();

    String content = Files.readString(clientDataPath);
    assertThat(content).contains("implements SpecificRecord");
  }

  @Test
  void stringTypeConfiguredAsString() throws IOException {
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

    GradleRunner.create()
        .forwardOutput()
        .withPluginClasspath()
        .withProjectDir(projectDir)
        .withArguments("generateAvroJava")
        .build();

    Path generatedDir =
        projectDir.toPath().resolve("build/generated/sources/avro/java/main");
    Path clientDataPath = generatedDir.resolve("org/kaaproject/kaa/common/channels/ClientData.java");

    assertThat(Files.exists(clientDataPath)).isTrue();

    String content = Files.readString(clientDataPath);
    // Plugin should configure String type (not Utf8)
    assertThat(content).contains("private String");
  }

  @Test
  void generatedCodeFollowsJavaNamingConventions() throws IOException {
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
        .withArguments("generateAvroJava")
        .build();

    Path generatedDir =
        projectDir.toPath().resolve("build/generated/sources/avro/java/main");
    boolean hasPascalCase =
        Files.walk(generatedDir)
            .anyMatch(
                p ->
                    p.toString().endsWith(".java")
                        && Character.isUpperCase(p.getFileName().toString().charAt(0)));

    assertThat(hasPascalCase).isTrue();
  }

  @Test
  void generatedClassesAreSerializable() throws IOException {
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

    GradleRunner.create()
        .forwardOutput()
        .withPluginClasspath()
        .withProjectDir(projectDir)
        .withArguments("generateAvroJava")
        .build();

    Path generatedDir =
        projectDir.toPath().resolve("build/generated/sources/avro/java/main");
    Path clientDataPath = generatedDir.resolve("org/kaaproject/kaa/common/channels/ClientData.java");

    assertThat(Files.exists(clientDataPath)).isTrue();

    String content = Files.readString(clientDataPath);
    assertThat(content).contains("java.io.Serializable");
  }

  @Test
  void extractedSchemasAreFlattened() throws IOException {
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

    File schemasDir = new File(projectDir, "build/kaa-schemas");
    assertThat(schemasDir.exists()).isTrue();

    // Schemas should be flattened (no subdirectories in kaa-schemas)
    boolean hasSubdirectories =
        Files.list(schemasDir.toPath()).anyMatch(p -> Files.isDirectory(p));

    assertThat(hasSubdirectories).isFalse();
  }
}
