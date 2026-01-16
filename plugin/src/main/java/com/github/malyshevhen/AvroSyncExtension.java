package com.github.malyshevhen;

import org.gradle.api.provider.Property;

public interface AvroSyncExtension {

  Property<String> getAvroSchemaSrc();

  Property<String> getGeneratedSrc();
}