package com.github.malyshevhen.gradle.avro;

import java.io.File;
import java.net.URL;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RelativePath;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

/**
 * A Gradle task that extracts Avro schema files from the plugin's internal resources.
 *
 * <p>This task is cacheable and extracts .avsc files from either:
 *
 * <ul>
 *   <li>The plugin JAR (when running in production)
 *   <li>The filesystem (when running in development)
 * </ul>
 *
 * Extracted schemas are written to the directory specified by {@link #getOutputDirectory()}.
 */
@CacheableTask
public abstract class ExtractKaaSchemasTask extends DefaultTask {

  /**
   * The output directory where extracted Avro schema files will be written.
   *
   * @return the directory property for the output location
   */
  @OutputDirectory
  public abstract DirectoryProperty getOutputDirectory();

  /**
   * Extracts Avro schema files from the plugin's internal resources to the output directory.
   *
   * <p>This method locates the bundled schemas within the plugin JAR or on the filesystem, copies
   * all .avsc files to the output directory, and flattens the directory structure so all schema
   * files are placed directly in the output directory.
   *
   * @throws RuntimeException if the internal resource path cannot be located
   */
  @TaskAction
  public void extract() {
    String resourcePath = Constants.SCHEMAS_RESOURCE_PATH;
    URL resourceUrl = getClass().getClassLoader().getResource(resourcePath);

    if (resourceUrl == null) {
      throw new RuntimeException("Missing internal resource: " + resourcePath);
    }

    getProject()
        .copy(
            spec -> {
              if ("jar".equals(resourceUrl.getProtocol())) {
                String path = resourceUrl.getPath();
                String jarPath = path.substring(5, path.indexOf("!"));
                spec.from(
                    getProject().zipTree(new File(jarPath)),
                    jarSpec -> {
                      jarSpec.include(resourcePath + "/**/*.avsc");
                      jarSpec.eachFile(
                          fcd -> fcd.setRelativePath(new RelativePath(true, fcd.getName())));
                    });
              } else {
                spec.from(
                    new File(resourceUrl.getPath()),
                    dirSpec -> {
                      dirSpec.include("**/*.avsc");
                      dirSpec.eachFile(
                          fcd -> fcd.setRelativePath(new RelativePath(true, fcd.getName())));
                    });
              }
              spec.setIncludeEmptyDirs(false);
              spec.into(getOutputDirectory());
            });
  }
}
