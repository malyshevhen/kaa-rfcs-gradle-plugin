package com.github.malyshevhen;

import com.github.davidmc24.gradle.plugin.avro.GenerateAvroJavaTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.file.RelativePath;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.Copy;
import org.gradle.api.tasks.TaskProvider;

public class KaaRfcsGradlePluginPlugin implements Plugin<Project> {

  @Override
  public void apply(Project project) {
    // Apply the official Avro plugin that we are wrapping
    project.getPluginManager().apply("com.github.davidmc24.gradle.plugin.avro");

    // Register the extension
    AvroSyncExtension extension = project.getExtensions().create("githubAvro", AvroSyncExtension.class);

    // Default paths in the consumer project
    extension.getAvroSchemaSrc().convention(
        project.getLayout().getBuildDirectory().dir("kaa-schemas").get().getAsFile().getPath()
    );
    extension.getGeneratedSrc().convention(
        project.getLayout().getBuildDirectory().dir("generated/sources/avro/java/test").get().getAsFile().getPath()
    );

    // 1. Task to extract schemas from the Plugin's JAR to the consumer's build folder
    TaskProvider<Copy> extractTask = project.getTasks().register("extractKaaSchemas", Copy.class, task -> {
      task.setGroup("avro");
      task.setDescription("Extracts bundled Kaa RFC Avro schemas from the plugin JAR.");

      // Find the schemas inside the JAR's resources
      task.from(project.zipTree(getJarLocation()), copySpec -> {
        copySpec.include("avro-schemas/**/*.avsc");
        // Flatten: remove '0004/', '0006/', etc., and put all .avsc in the root of the destination
        copySpec.eachFile(fcd -> fcd.setRelativePath(new RelativePath(true, fcd.getName())));
        copySpec.setIncludeEmptyDirs(false);
      });

      task.into(extension.getAvroSchemaSrc());
    });

    // 2. Configure the GenerateAvroJavaTask
    project.getTasks().withType(GenerateAvroJavaTask.class).configureEach(avroTask -> {
      avroTask.dependsOn(extractTask);
      // Tell Avro to look at our extracted folder
      avroTask.source(extension.getAvroSchemaSrc());
      avroTask.setOutputDir(project.file(extension.getGeneratedSrc().get()));

      // Apply your convention of using String instead of CharSequence
      avroTask.getConventionMapping().map("stringType", () -> "String");
    });

    // 3. Add generated code to the Test SourceSet automatically
    project.getExtensions().getByType(JavaPluginExtension.class).getSourceSets().configureEach(ss -> {
      if (ss.getName().equals("test")) {
        ss.getJava().srcDir(extension.getGeneratedSrc());
      }
    });
  }

  /**
   * Helper to find the JAR file containing this plugin at runtime
   */
  private java.io.File getJarLocation() {
    try {
      return new java.io.File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
    } catch (Exception e) {
      throw new RuntimeException("Could not determine Plugin JAR location", e);
    }
  }
}