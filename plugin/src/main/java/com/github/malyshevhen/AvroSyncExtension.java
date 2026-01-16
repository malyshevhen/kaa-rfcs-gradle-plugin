package com.github.malyshevhen;

import org.gradle.api.file.DirectoryProperty;

public abstract class AvroSyncExtension {

  public abstract DirectoryProperty getAvroSchemaSrc();

  public abstract DirectoryProperty getGeneratedSrc();
}
