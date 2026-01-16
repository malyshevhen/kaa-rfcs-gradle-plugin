package com.github.malyshevhen.gradle.avro;

import java.io.File;
import java.net.URL;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RelativePath;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

@CacheableTask
public abstract class ExtractKaaSchemasTask extends DefaultTask {

  @OutputDirectory
  public abstract DirectoryProperty getOutputDirectory();

  @TaskAction
  public void extract() {
    String resourcePath = Constants.SCHEMAS_RESOURCE_PATH;
    URL resourceUrl = getClass().getClassLoader().getResource(resourcePath);

    if (resourceUrl == null) {
      throw new RuntimeException("Missing internal resource: " + resourcePath);
    }

    getProject().copy(spec -> {
      if ("jar".equals(resourceUrl.getProtocol())) {
        String path = resourceUrl.getPath();
        String jarPath = path.substring(5, path.indexOf("!"));
        spec.from(getProject().zipTree(new File(jarPath)), jarSpec -> {
          jarSpec.include(resourcePath + "/**/*.avsc");
          jarSpec.eachFile(fcd -> fcd.setRelativePath(new RelativePath(true, fcd.getName())));
        });
      } else {
        spec.from(new File(resourceUrl.getPath()), dirSpec -> {
          dirSpec.include("**/*.avsc");
          dirSpec.eachFile(fcd -> fcd.setRelativePath(new RelativePath(true, fcd.getName())));
        });
      }
      spec.setIncludeEmptyDirs(false);
      spec.into(getOutputDirectory());
    });
  }
}