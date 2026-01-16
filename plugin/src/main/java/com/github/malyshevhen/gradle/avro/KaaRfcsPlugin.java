package com.github.malyshevhen.gradle.avro;

import static com.github.malyshevhen.gradle.avro.Constants.AVRO_PLUGIN_ID;
import static com.github.malyshevhen.gradle.avro.Constants.AVRO_RUNTIME_DEPENDENCY;
import static com.github.malyshevhen.gradle.avro.Constants.DEFAULT_GEN_DIR;
import static com.github.malyshevhen.gradle.avro.Constants.DEFAULT_SCHEMA_DIR;
import static com.github.malyshevhen.gradle.avro.Constants.EXTRACT_TASK_NAME;
import static com.github.malyshevhen.gradle.avro.Constants.GROUP;
import static com.github.malyshevhen.gradle.avro.Constants.IDEA_MODULE_TASK;
import static com.github.malyshevhen.gradle.avro.Constants.MAIN_SOURCE_SET;

import com.github.davidmc24.gradle.plugin.avro.GenerateAvroJavaTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.TaskProvider;

/**
 * A Gradle plugin that bundles Kaa RFC Avro schemas and automates Java SpecificRecord generation.
 *
 * <p>This plugin extracts internal Avro schemas from the plugin JAR, generates Java source files
 * using the gradle-avro-plugin, and integrates the generated sources into the build lifecycle. The
 * plugin applies the Avro plugin, Java plugin, and IDEA plugin automatically.
 */
public class KaaRfcsPlugin implements Plugin<Project> {

  @Override
  public void apply(Project project) {
    project.getPluginManager().apply(AVRO_PLUGIN_ID);
    project.getPluginManager().apply("java");
    project.getPluginManager().apply("idea");

    AvroSyncExtension extension =
        project.getExtensions().create("kaaAvro", AvroSyncExtension.class);
    configureDefaults(project, extension);

    TaskProvider<ExtractKaaSchemasTask> extractTask = registerExtractTask(project, extension);

    project.afterEvaluate(
        p -> {
          try {
            p.getTasks().named(IDEA_MODULE_TASK).configure(module -> module.dependsOn(extractTask));
          } catch (org.gradle.api.UnknownTaskException e) {
            // IDEA module task not available (IDEA plugin not applied), silently skip
          }
        });

    configureAvroGeneration(project, extractTask, extension);
    configureProjectLifecycle(project, extension);
    configureCleanup(project, extension);
  }

  private void configureDefaults(Project project, AvroSyncExtension extension) {
    extension
        .getAvroSchemaSrc()
        .convention(project.getLayout().getBuildDirectory().dir(DEFAULT_SCHEMA_DIR));
    extension
        .getGeneratedSrc()
        .convention(project.getLayout().getBuildDirectory().dir(DEFAULT_GEN_DIR));
  }

  private TaskProvider<ExtractKaaSchemasTask> registerExtractTask(
      Project project, AvroSyncExtension extension) {
    return project
        .getTasks()
        .register(
            EXTRACT_TASK_NAME,
            ExtractKaaSchemasTask.class,
            task -> {
              task.setGroup(GROUP);
              task.setDescription("Extracts internal Avro schemas.");
              task.getOutputDirectory().set(extension.getAvroSchemaSrc());
            });
  }

  private void configureAvroGeneration(
      Project project,
      TaskProvider<ExtractKaaSchemasTask> extractTask,
      AvroSyncExtension extension) {
    project
        .getTasks()
        .withType(GenerateAvroJavaTask.class)
        .configureEach(
            avroTask -> {
              avroTask.source(extractTask);
              avroTask.dependsOn(extractTask);
              if (extension.getGeneratedSrc().isPresent()) {
                avroTask.setOutputDir(project.file(extension.getGeneratedSrc().get()));
              }
              avroTask.getConventionMapping().map("stringType", () -> "String");
            });
  }

  private void configureProjectLifecycle(Project project, AvroSyncExtension extension) {
    project.getDependencies().add("implementation", AVRO_RUNTIME_DEPENDENCY);

    project
        .getExtensions()
        .getByType(JavaPluginExtension.class)
        .getSourceSets()
        .configureEach(
            ss -> {
              if (ss.getName().equals(MAIN_SOURCE_SET)) {
                ss.getJava().srcDir(extension.getGeneratedSrc());
              }
            });

    project
        .getTasks()
        .named("compileJava")
        .configure(
            compileJava ->
                compileJava.dependsOn(project.getTasks().withType(GenerateAvroJavaTask.class)));
  }

  /** Ensures that 'clean' task removes the plugin's generated directories. */
  private void configureCleanup(Project project, AvroSyncExtension extension) {
    project
        .getTasks()
        .named("clean")
        .configure(
            cleanTask ->
                cleanTask.doFirst(
                    t -> {
                      project.delete(extension.getAvroSchemaSrc().get());
                      project.delete(extension.getGeneratedSrc().get());
                    }));
  }
}
