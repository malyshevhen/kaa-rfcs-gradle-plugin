package com.github.malyshevhen.gradle.avro;

/**
 * Constants used throughout the Kaa RFCs Gradle plugin.
 *
 * <p>This class contains plugin identifiers, task names, directory paths, and dependency
 * specifications. It is designed for use with static imports.
 */
public final class Constants {
  /** The Gradle task group for all plugin-specific tasks. */
  public static final String GROUP = "avro";

  /** The name of the task that extracts internal Avro schemas. */
  public static final String EXTRACT_TASK_NAME = "extractKaaSchemas";

  /** The plugin ID of the gradle-avro-plugin used for schema generation. */
  public static final String AVRO_PLUGIN_ID = "com.github.davidmc24.gradle.plugin.avro";

  /** The resource path within the plugin JAR where Avro schemas are stored. */
  public static final String SCHEMAS_RESOURCE_PATH = "avro-schemas";

  /** Default directory path for extracted schema files relative to the build directory. */
  public static final String DEFAULT_SCHEMA_DIR = "kaa-schemas";

  /** Default directory path for generated Java files relative to the build directory. */
  public static final String DEFAULT_GEN_DIR = "generated/sources/avro/java/main";

  /** Maven coordinate for the Avro runtime dependency required by generated code. */
  public static final String AVRO_RUNTIME_DEPENDENCY = "org.apache.avro:avro:1.11.3";

  /** The name of the IDEA module task. */
  public static final String IDEA_MODULE_TASK = "ideaModule";

  /** The name of the main source set. */
  public static final String MAIN_SOURCE_SET = "main";

  private Constants() {}
}
