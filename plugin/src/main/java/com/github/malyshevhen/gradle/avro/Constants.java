package com.github.malyshevhen.gradle.avro;

public final class Constants {
    public static final String GROUP = "avro";
    public static final String EXTRACT_TASK_NAME = "extractKaaSchemas";
    public static final String AVRO_PLUGIN_ID = "com.github.davidmc24.gradle.plugin.avro";
    public static final String SCHEMAS_RESOURCE_PATH = "avro-schemas";
    
    // Default paths
    public static final String DEFAULT_SCHEMA_DIR = "kaa-schemas";
    public static final String DEFAULT_GEN_DIR = "generated/sources/avro/java/main";
    
    // Dependencies
    public static final String AVRO_RUNTIME_DEPENDENCY = "org.apache.avro:avro:1.11.3";

    private Constants() {}
}