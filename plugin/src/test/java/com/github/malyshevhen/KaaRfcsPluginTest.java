package com.github.malyshevhen;

import java.io.IOException;
import java.nio.file.Files;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

class KaaRfcsPluginTest {

  @TempDir
  File projectDir;

  @Test
  void pluginRegistersExtensionAndTasks() {
    Project project = ProjectBuilder.builder().withProjectDir(projectDir).build();
    project.getPlugins().apply("java");
    project.getPlugins().apply("kaa.rfcs-gradle-plugin");

    assertThat(project.getExtensions().findByName("kaaAvro")).isNotNull();
    assertThat(project.getTasks().findByName("extractKaaSchemas")).isNotNull();
  }

  @Test
  void appliesAvroPluginAutomatically() {
    Project project = ProjectBuilder.builder().withProjectDir(projectDir).build();
    project.getPlugins().apply("java");
    project.getPlugins().apply("kaa.rfcs-gradle-plugin");

    assertThat(project.getPlugins().hasPlugin("com.github.davidmc24.gradle.plugin.avro"))
        .isTrue();
  }

  @Test
  void appliesJavaPluginAutomatically() {
    Project project = ProjectBuilder.builder().withProjectDir(projectDir).build();
    project.getPlugins().apply("kaa.rfcs-gradle-plugin");

    assertThat(project.getPlugins().hasPlugin("java")).isTrue();
  }

  @Test
  void appliesIdeaPluginAutomatically() {
    Project project = ProjectBuilder.builder().withProjectDir(projectDir).build();
    project.getPlugins().apply("kaa.rfcs-gradle-plugin");

    assertThat(project.getPlugins().hasPlugin("idea")).isTrue();
  }

  @Test
  void addsAvroRuntimeDependency() {
    Project project = ProjectBuilder.builder().withProjectDir(projectDir).build();
    project.getPlugins().apply("java");
    project.getPlugins().apply("kaa.rfcs-gradle-plugin");

    assertThat(
            project
                .getConfigurations()
                .getByName("implementation")
                .getDependencies()
                .stream()
                .anyMatch(d -> d.getName().contains("avro")))
        .isTrue();
  }

  @Test
  void registersExtensionWithCorrectName() {
    Project project = ProjectBuilder.builder().withProjectDir(projectDir).build();
    project.getPlugins().apply("java");
    project.getPlugins().apply("kaa.rfcs-gradle-plugin");

    Object extension = project.getExtensions().findByName("kaaAvro");
    assertThat(extension).isNotNull();
    assertThat(extension.getClass().getSimpleName()).contains("Extension");
  }

  @Test
  void registersExtractKaaSchemasTask() {
    Project project = ProjectBuilder.builder().withProjectDir(projectDir).build();
    project.getPlugins().apply("java");
    project.getPlugins().apply("kaa.rfcs-gradle-plugin");

    assertThat(project.getTasks().findByName("extractKaaSchemas")).isNotNull();
  }

  @Test
  void registersGenerateAvroJavaTask() {
    Project project = ProjectBuilder.builder().withProjectDir(projectDir).build();
    project.getPlugins().apply("java");
    project.getPlugins().apply("kaa.rfcs-gradle-plugin");

    assertThat(project.getTasks().findByName("generateAvroJava")).isNotNull();
  }

  @Test
  void pluginIsIdempotent() {
    Project project = ProjectBuilder.builder().withProjectDir(projectDir).build();
    project.getPlugins().apply("kaa.rfcs-gradle-plugin");
    project.getPlugins().apply("kaa.rfcs-gradle-plugin");

    assertThat(project.getPlugins().hasPlugin("kaa.rfcs-gradle-plugin")).isTrue();
  }

  @Test
  void extensionHasDefaultValues() {
    Project project = ProjectBuilder.builder().withProjectDir(projectDir).build();
    project.getPlugins().apply("java");
    project.getPlugins().apply("kaa.rfcs-gradle-plugin");

    Object extension = project.getExtensions().findByName("kaaAvro");
    assertThat(extension).isNotNull();
  }

  @Test
  void extractKaaSchemasTaskHasCorrectGroup() {
    Project project = ProjectBuilder.builder().withProjectDir(projectDir).build();
    project.getPlugins().apply("java");
    project.getPlugins().apply("kaa.rfcs-gradle-plugin");

    var task = project.getTasks().findByName("extractKaaSchemas");
    assertThat(task).isNotNull();
    assertThat(task.getGroup()).isEqualTo("avro");
  }

  @Test
  void extractKaaSchemasTaskHasDescription() {
    Project project = ProjectBuilder.builder().withProjectDir(projectDir).build();
    project.getPlugins().apply("java");
    project.getPlugins().apply("kaa.rfcs-gradle-plugin");

    var task = project.getTasks().findByName("extractKaaSchemas");
    assertThat(task).isNotNull();
    assertThat(task.getDescription()).isEqualTo("Extracts internal Avro schemas.");
  }

  @Test
  void pluginIdIsCorrect() {
    assertThat("kaa.rfcs-gradle-plugin").isNotNull();
    assertThat("kaa.rfcs-gradle-plugin").contains("kaa");
    assertThat("kaa.rfcs-gradle-plugin").contains("plugin");
  }

  @Test
  void pluginImplementationClassExists() throws ClassNotFoundException {
    Class<?> pluginClass =
        Class.forName("com.github.malyshevhen.gradle.avro.KaaRfcsPlugin");
    assertThat(pluginClass).isNotNull();
    assertThat(pluginClass.getInterfaces()[0].getSimpleName()).isEqualTo("Plugin");
  }
}
