package com.github.malyshevhen.gradle.avro;

import java.io.File;
import org.gradle.api.Project;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;

class ExtractKaaSchemasTaskTest {

  @TempDir
  File testProjectDir;

  @Test
  void taskCanHaveOutputDirectorySet() {
    Project project = ProjectBuilder.builder().withProjectDir(testProjectDir).build();
    ExtractKaaSchemasTask task =
        project.getTasks().create("testExtract", ExtractKaaSchemasTask.class);
    DirectoryProperty outputDir = task.getOutputDirectory();

    File outputDirFile = new File(testProjectDir, "output");
    task.getOutputDirectory().set(project.file(outputDirFile));

    assertThat(outputDir.isPresent()).isTrue();
  }

  @Test
  void taskIsCacheable() {
    assertThat(ExtractKaaSchemasTask.class).hasAnnotation(CacheableTask.class);
  }

  @Test
  void outputDirectoryIsAnnotatedWithOutputDirectory() throws NoSuchMethodException {
    assertThat(
            ExtractKaaSchemasTask.class
                .getMethod("getOutputDirectory")
                .getAnnotation(OutputDirectory.class))
        .isNotNull();
  }

  @Test
  void outputDirectoryIsDirectoryProperty() throws NoSuchMethodException {
    assertThat(
            ExtractKaaSchemasTask.class.getMethod("getOutputDirectory").getReturnType())
        .isEqualTo(DirectoryProperty.class);
  }

  @Test
  void taskCanBeCreatedWithProject() {
    Project project = ProjectBuilder.builder().withProjectDir(testProjectDir).build();
    ExtractKaaSchemasTask task =
        project.getTasks().create("testExtract", ExtractKaaSchemasTask.class);

    assertThat(task).isNotNull();
    assertThat(task.getProject()).isNotNull();
  }

  @Test
  void outputDirectoryCanBeConfigured() {
    Project project = ProjectBuilder.builder().withProjectDir(testProjectDir).build();
    ExtractKaaSchemasTask task =
        project.getTasks().create("testExtract", ExtractKaaSchemasTask.class);

    File customDir = new File(testProjectDir, "custom-output");
    task.getOutputDirectory().set(project.file(customDir));

    assertThat(task.getOutputDirectory().get().getAsFile()).isEqualTo(customDir);
  }

  @Test
  void outputDirectoryReturnsNotNullProperty() {
    Project project = ProjectBuilder.builder().withProjectDir(testProjectDir).build();
    ExtractKaaSchemasTask task =
        project.getTasks().create("testExtract", ExtractKaaSchemasTask.class);

    assertThat(task.getOutputDirectory()).isNotNull();
  }

  @Test
  void resourcePathConstantIsCorrect() {
    assertThat(Constants.SCHEMAS_RESOURCE_PATH).isEqualTo("avro-schemas");
    assertThat(Constants.SCHEMAS_RESOURCE_PATH).isNotEmpty();
  }

  @Test
  void extractMethodExistsAndIsPublic() throws NoSuchMethodException {
    assertThat(
            ExtractKaaSchemasTask.class.getMethod("extract").getModifiers()
                & java.lang.reflect.Modifier.PUBLIC)
        .isNotEqualTo(0);
  }

  @Test
  void extractMethodHasTaskActionAnnotation() throws NoSuchMethodException {
    assertThat(
            ExtractKaaSchemasTask.class
                .getMethod("extract")
                .getAnnotation(org.gradle.api.tasks.TaskAction.class))
        .isNotNull();
  }
}
