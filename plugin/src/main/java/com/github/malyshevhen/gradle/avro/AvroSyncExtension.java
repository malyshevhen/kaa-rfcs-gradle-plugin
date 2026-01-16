package com.github.malyshevhen.gradle.avro;

import org.gradle.api.file.DirectoryProperty;

public abstract class AvroSyncExtension {

  public abstract DirectoryProperty getAvroSchemaSrc();

  public abstract DirectoryProperty getGeneratedSrc();
}
