package com.github.malyshevhen;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KaaRfcsGradlePluginFunctionalTest {

  @TempDir File projectDir;

  @Test
  void pluginExtractsAndGeneratesJava() throws IOException {
    // 1. Set up a dummy consumer project
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
    Files.writeString(
        projectDir.toPath().resolve("settings.gradle"), "rootProject.name = 'kaa-test'");

    // 2. Run the full generation task
    BuildResult result =
        GradleRunner.create()
            .forwardOutput()
            .withPluginClasspath()
            .withProjectDir(projectDir)
            .withArguments("generateAvroJava")
            .build();

    // 3. Assert Task Success - check actual task outcome
    assertEquals(
        org.gradle.testkit.runner.TaskOutcome.SUCCESS,
        result.task(":generateAvroJava").getOutcome(),
        "generateAvroJava task should succeed");

    // 4. VERIFICATION: Check extracted .avsc files
    File extractedSchema = new File(projectDir, "build/kaa-schemas/0004-client-data.avsc");
    assertTrue(extractedSchema.exists(), "Schema file 0004-client-data.avsc was not extracted");

    // 5. VERIFICATION: Check generated .java files
    Path generatedJavaFile =
        projectDir
            .toPath()
            .resolve(
                "build/generated/sources/avro/java/main/org/kaaproject/kaa/common/channels/ClientData.java");

    File generatedDir = new File(projectDir, "build/generated/sources/avro/java/main");
    assertTrue(
        generatedDir.exists() && generatedDir.isDirectory(),
        "Generated Java directory does not exist");

    // Assert that at least some .java files were created
    long javaFileCount =
        Files.walk(generatedDir.toPath()).filter(p -> p.toString().endsWith(".java")).count();

    assertTrue(javaFileCount > 0, "No Java files were generated from the Avro schemas");
    System.out.println("Successfully verified " + javaFileCount + " generated Java files.");
  }
}
