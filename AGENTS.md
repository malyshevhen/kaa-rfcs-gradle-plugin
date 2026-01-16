# Kaa RFCs Gradle Plugin - Agent Guidelines

This guide is for agentic coding assistants working on this repository.

---

## Build and Test Commands

### Core Commands
- `./gradlew build` - Build the project (includes test, functionalTest, and jar)
- `./gradlew clean` - Clean build artifacts
- `./gradlew check` - Run all validation tasks (test + functionalTest)
- `./gradlew :plugin:jar` - Build only the plugin JAR

### Testing
- `./gradlew test` - Run unit tests
- `./gradlew functionalTest` - Run functional/integration tests (uses Gradle TestKit)
- `./gradlew functionalTest --info` - Run functional tests with verbose logging

### Running a Single Test
- `./gradlew test --tests <ClassName>` - Run a specific unit test class
- `./gradlew functionalTest --tests <ClassName>` - Run a specific functional test class
- `./gradlew test --tests <ClassName>.<methodName>` - Run a specific test method

### Publishing
- `./gradlew publishToMavenLocal` - Publish to local Maven repository for testing
- `./gradlew publish` - Publish to GitHub Packages (requires GITHUB_TOKEN)

---

## Code Style Guidelines

### General Formatting
- **Indentation**: 2 spaces (no tabs)
- **Line length**: No strict limit, but keep lines readable
- **Blank lines**: One blank line between methods, two between logical sections

### Package Structure
- **Main plugin code**: `com.github.malyshevhen.gradle.avro`
- **Test code**: `com.github.malyshevhen` (without the `gradle.avro` suffix)
- **Constants**: All constants live in `com.github.malyshevhen.gradle.avro.Constants`

### Imports
- Use static imports for constants: `import static com.github.malyshevhen.gradle.avro.Constants.*;`
- Standard imports for regular classes
- No wildcard imports except for constants

### Naming Conventions
- **Classes**: PascalCase (e.g., `KaaRfcsPlugin`, `ExtractKaaSchemasTask`)
- **Methods**: camelCase (e.g., `configureDefaults`, `registerExtractTask`)
- **Constants**: UPPER_SNAKE_CASE (e.g., `AVRO_PLUGIN_ID`, `DEFAULT_SCHEMA_DIR`)
- **Tasks**: camelCase (e.g., `extractKaaSchemas`, `generateAvroJava`)
- **Plugin ID**: kebab-case with dots (e.g., `kaa.rfcs-gradle-plugin`)

### Gradle Plugin Development
- **Plugin class**: Implement `org.gradle.api.Plugin<Project>`
- **Task class**: Extend `org.gradle.api.DefaultTask`
- **Extension class**: Abstract class with abstract getter methods
- **Task inputs/outputs**: Use `DirectoryProperty`, `RegularFileProperty` types
- **Incremental builds**: Use `@CacheableTask` annotation for tasks
- **Task actions**: Annotate with `@TaskAction`
- **Output directories**: Annotate with `@OutputDirectory`
- **Task registration**: Use `project.getTasks().register()` instead of `create()`
- **Task configuration**: Use lambda expressions for task configuration

### Task Dependencies
- Make tasks depend on `TaskProvider<T>`, not task instances directly
- Use `project.getTasks().withType(TaskClass).configureEach()` for configuring all tasks of a type

### Error Handling
- Throw `RuntimeException` for unrecoverable errors
- Check for null/missing resources and throw descriptive exceptions

### Testing
- **Unit tests**: JUnit 5 with `@Test` annotation
- **Functional tests**: Use `org.gradle.testkit.runner.GradleRunner`
- **Temporary directories**: Use `@TempDir` annotation from JUnit 5
- **Assertions**: Use `org.junit.jupiter.api.Assertions`
- **Test location**: `plugin/src/test/java` for unit, `plugin/src/functionalTest/java` for functional

### Code Organization
- Keep constants in a dedicated `Constants` class with a private constructor
- Separate configuration logic into private methods within the plugin class
- Use JavaDocs sparingly - only when the behavior is non-obvious
- Prefer method references over lambdas when method is clear: `project.getPluginManager()::apply`

### Dependencies
- Managed via `gradle/libs.versions.toml` (version catalog)
- Use `libs.<dependency>` format in build.gradle
- Avro runtime dependency version: 1.11.3

### IDE Configuration
- Checkstyle configured with Google and Sun checks (see `.idea/checkstyle-idea.xml`)
- Java version: 21
- Gradle version: 8.5

### Functional Test Requirements
- Tests must create temporary project directories with `@TempDir`
- Write minimal `build.gradle` and `settings.gradle` files
- Use `GradleRunner.create()` with `.withPluginClasspath()` for testing
- Run tasks with `.build()` and verify output or file existence

### Git Submodule
- Upstream schemas tracked as Git submodule at `plugin/src/main/resources/avro-schemas`
- Update with: `git submodule update --remote`
- CI automatically initializes submodules recursively

### Important Notes
- This is a Gradle plugin that bundles Avro schemas internally
- Uses `com.github.davidmc24.gradle.plugin:gradle-avro-plugin` as a dependency
- Automatically injects Avro runtime dependency
- Registers generated sources with the main Java SourceSet
- Hooks into `compileJava` task lifecycle
