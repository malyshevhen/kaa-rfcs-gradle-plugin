package com.github.malyshevhen;

import com.github.davidmc24.gradle.plugin.avro.GenerateAvroJavaTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.file.RelativePath;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.Copy;
import java.io.File;
import java.net.URL;

public class KaaRfcsGradlePluginPlugin implements Plugin<Project> {
  @Override
  public void apply(Project project) {
    project.getPluginManager().apply("com.github.davidmc24.gradle.plugin.avro");

    AvroSyncExtension extension = project.getExtensions().create("githubAvro", AvroSyncExtension.class);

    extension.getAvroSchemaSrc().convention(
        project.getLayout().getBuildDirectory().dir("kaa-schemas").get().getAsFile().getPath()
    );
    extension.getGeneratedSrc().convention(
        project.getLayout().getBuildDirectory().dir("generated/sources/avro/java/test").get().getAsFile().getPath()
    );

    var extractTask = project.getTasks().register("extractKaaSchemas", Copy.class, task -> {
      task.setGroup("avro");

      // Get the location of the resource in the classpath
      URL resourceUrl = getClass().getClassLoader().getResource("avro-schemas");
      if (resourceUrl == null) {
        throw new RuntimeException("Could not find 'avro-schemas' in plugin classpath");
      }

      if (resourceUrl.getProtocol().equals("jar")) {
        // CASE 1: Running from a JAR (Production)
        String jarPath = resourceUrl.getPath().substring(5, resourceUrl.getPath().indexOf("!"));
        task.from(project.zipTree(new File(jarPath)), copySpec -> {
          copySpec.include("avro-schemas/**/*.avsc");
          copySpec.eachFile(fcd -> fcd.setRelativePath(new RelativePath(true, fcd.getName())));
        });
      } else {
        // CASE 2: Running from a Directory (Functional Tests / IDE)
        task.from(new File(resourceUrl.getPath()), copySpec -> {
          copySpec.include("**/*.avsc");
          copySpec.eachFile(fcd -> fcd.setRelativePath(new RelativePath(true, fcd.getName())));
        });
      }

      task.setIncludeEmptyDirs(false);
      task.into(extension.getAvroSchemaSrc());
    });

    project.getTasks().withType(GenerateAvroJavaTask.class).configureEach(avroTask -> {
      avroTask.dependsOn(extractTask);
      avroTask.source(extension.getAvroSchemaSrc());
      avroTask.setOutputDir(project.file(extension.getGeneratedSrc().get()));
      avroTask.getConventionMapping().map("stringType", () -> "String");
    });

    project.getExtensions().getByType(JavaPluginExtension.class).getSourceSets().configureEach(ss -> {
      if (ss.getName().equals("test")) {
        ss.getJava().srcDir(extension.getGeneratedSrc());
      }
    });
  }
}