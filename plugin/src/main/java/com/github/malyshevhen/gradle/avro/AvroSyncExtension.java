package com.github.malyshevhen.gradle.avro;

import org.gradle.api.file.DirectoryProperty;

/**
 * Gradle extension for configuring the Kaa RFCs Avro plugin.
 *
 * <p>This extension allows customization of where extracted Avro schemas and generated Java source
 * files are placed within the project. Both properties have sensible defaults but can be overridden
 * via the {@code kaaAvro} extension block in the build script.
 *
 * <p>Example configuration:
 *
 * <pre>
 * kaaAvro {
 *     avroSchemaSrc = layout.buildDirectory.dir("custom-schemas")
 *     generatedSrc = layout.buildDirectory.dir("custom-java")
 * }
 * </pre>
 */
public abstract class AvroSyncExtension {

  /**
   * The directory where extracted .avsc schema files will be placed.
   *
   * <p>Defaults to {@code build/kaa-schemas}.
   *
   * @return the directory property for extracted schema files
   */
  public abstract DirectoryProperty getAvroSchemaSrc();

  /**
   * The directory where generated Java source files will be written.
   *
   * <p>Defaults to {@code build/generated/sources/avro/java/main}.
   *
   * @return the directory property for generated Java files
   */
  public abstract DirectoryProperty getGeneratedSrc();
}
