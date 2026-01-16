# Kaa RFCs Gradle Plugin

![Build Status](https://github.com/malyshevhen/kaa-rfcs-gradle-plugin/actions/workflows/ci.yml/badge.svg)
[![Release](https://img.shields.io/github/v/release/malyshevhen/kaa-rfcs-gradle-plugin?include_prereleases)](https://github.com/malyshevhen/kaa-rfcs-gradle-plugin/releases)
[![JitPack](https://jitpack.io/v/malyshevhen/kaa-rfcs-gradle-plugin.svg)](https://jitpack.io/github/malyshevhen/kaa-rfcs-gradle-plugin)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](https://opensource.org/licenses/MIT)

A zero-configuration Gradle plugin that bundles [Kaa RFC Avro Schemas](https://github.com/kaaproject/kaa-rfcs) and automates Java SpecificRecord generation within
the standard build lifecycle.

## Features

* **Internal Schema Bundling**: Avro schemas are packaged within the plugin JAR, eliminating the need for manual schema
  distribution across projects.
* **Lifecycle Automation**: The plugin hooks directly into the `compileJava` task. Code generation occurs automatically
  before the compilation phase begins.
* **Incremental Build Support**: Custom tasks utilize Gradle's Task Input/Output API to ensure compatibility with the
  build cache and incremental processing.
* **Automated Version Tracking**: A GitHub Actions watchdog monitors upstream schema changes daily, ensuring the plugin
  remains synchronized with the latest RFCs.

---

## Installation

As this plugin is hosted on [JitPack](https://jitpack.io/#malyshevhen/kaa-rfcs-gradle-plugin), the repository must be defined in your plugin management block.

### 1. Define Repository

**settings.gradle**

```groovy
pluginManagement {
    repositories {
        gradlePluginPortal()
        maven { url "https://jitpack.io" }
    }

    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == 'kaa.rfcs-gradle-plugin') {
                useModule("com.github.malyshevhen:kaa-rfcs-gradle-plugin:${requested.version}")
            }
        }
    }
}
```

### 2. Apply Plugin

**build.gradle**

```groovy
plugins {
    id 'kaa.rfcs-gradle-plugin' version 'v0.1.0'
}
```

---

## Configuration

The plugin provides sensible defaults but can be customized via the `kaaAvro` extension block:

```groovy
kaaAvro {
    // Directory for extracted .avsc files (Default: build/kaa-schemas)
    avroSchemaSrc = layout.buildDirectory.dir("custom-schemas")

    // Directory for generated Java sources (Default: build/generated/sources/avro/java/main)
    generatedSrc = layout.buildDirectory.dir("custom-java")
}
```

---

## Technical Architecture

The plugin orchestrates three primary phases to ensure type safety and schema consistency:

1. **Extraction**: The `extractKaaSchemas` task identifies the internal plugin resources and extracts them to the
   project build directory.
2. **Generation**: The `generateAvroJava` task consumes the extracted schemas to produce Java source files.
3. **Integration**: The generated path is registered as a source directory within the `main` SourceSet, and the
   `avro-runtime` dependency is automatically injected.

---

## Build and Release

### Submodule Management

This project utilizes a Git Submodule to track the upstream schema repository.

* To update locally: `git submodule update --remote`
* CI Automation: The repository includes a scheduled workflow that detects submodule updates, performs a version bump,
  and publishes a new release tag.

### Local Development

To install the plugin to your local Maven repository for testing:

```bash
./gradlew publishToMavenLocal
```

---

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for the full text.