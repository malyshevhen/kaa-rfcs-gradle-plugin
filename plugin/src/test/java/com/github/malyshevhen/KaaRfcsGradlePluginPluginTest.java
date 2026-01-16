package com.github.malyshevhen;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KaaRfcsGradlePluginPluginTest {

  @Test
  void pluginRegistersExtensionAndTasks() {
    // Create a test project and apply the plugin
    Project project = ProjectBuilder.builder().build();
    project.getPlugins().apply("java"); // Required by our plugin
    project.getPlugins().apply("kaa.rfcs-gradle-plugin");

    // Verify Extension
    assertNotNull(project.getExtensions().findByName("kaaAvro"));

    // Verify Extraction Task
    assertNotNull(project.getTasks().findByName("extractKaaSchemas"));

    // Verify Avro Plugin was applied by our plugin
    assertTrue(project.getPlugins().hasPlugin("com.github.davidmc24.gradle.plugin.avro"));
  }
}