![Maven Central Version](https://img.shields.io/maven-central/v/dev.limebeck.libs/common)

# microservices-libs

A collection of Kotlin libraries that I use to build microservices and back-end services. This is a multi-module, Gradle-based monorepo that provides reusable utilities for:
- Database access and migrations (JOOQ, Flyway, HikariCP, Postgres, H2)
- Kafka utilities (consumer/listener helpers, producer props)
- Common cross-platform helpers (logging facades, time, result types, scopes)
- Multiplatform test utils (runTest, await/assert helpers)

The modules can be used independently. Publishing is configured for Maven Central.


## Modules
- libs/common (Kotlin Multiplatform)
  - Common utilities used across services: logging facades (per platform), time helpers, result types, scopes, simple value generators, database abstractions, transaction helpers, etc.
- libs/database-jvm-flyway (JVM)
  - Flyway-based migration service wrapper for JVM projects.
- libs/database-jvm-jooq (JVM)
  - JOOQ-based database service and helpers for transactional operations.
  - Includes tests using H2 and Postgres via Testcontainers.
- libs/database-jvm-ktorm (JVM)
  - KTorm integration helpers for JVM projects.
- libs/kafka-utils (JVM)
  - Kafka listener utilities and producer/consumer properties helpers.
  - Includes tests using a Kafka Testcontainers image.
- libs/multiplatform-test-utils (Kotlin Multiplatform)
  - Common test utilities used by other modules (runTest, await/assert, etc.).


## Development requirements
- Java Development Kit (JDK): 17+ recommended (exact minimum not pinned here; verify per your toolchain) [TODO: confirm minimal JDK]
- Docker: required to run tests that use Testcontainers (Kafka/Postgres)
- No global Gradle installation is needed — use the provided Gradle Wrapper


## Getting started
Clone the repository and use the Gradle Wrapper for all tasks.

- Build all modules:
  ./gradlew build

- Run tests:
  ./gradlew test
  # Some JVM tests spin up Docker containers via Testcontainers.

- Generate documentation (Dokka):
  ./gradlew dokkaHtml
  # Dokka is applied to subprojects; run per module or from root to build all.

- Check for dependency updates (Gradle Versions plugin):
  ./gradlew dependencyUpdates

- Publish to local Maven (for trying modules in other projects):
  ./gradlew publishToMavenLocal


## Using the libraries (coordinates)
Artifacts are published under the group:
- `dev.limebeck.libs`

Examples of artifact IDs (per module and target):
- common (metadata), common-jvm, common-js, common-native-(linux|macOS|win)
- database-jvm-flyway
- database-jvm-jooq
- database-jvm-ktorm
- kafka-utils

Example (Gradle Kotlin DSL):
- `implementation("dev.limebeck.libs:common:<version>")`
- `implementation("dev.limebeck.libs:database-jvm-jooq:<version>")`
- `implementation("dev.limebeck.libs:kafka-utils:<version>")`

The current project version is defined in gradle.properties as libVersion (e.g., 0.0.1). For released versions, refer to the repository releases or Maven Central.


## Project structure (high level)
- README.MD — this file
- build.gradle.kts — root Gradle build, common publishing and Dokka config
- gradle/libs.versions.toml — centralized dependency and plugin versions
- settings.gradle.kts — includes subprojects
- libs/
  - common/
  - database-jvm-flyway/
  - database-jvm-jooq/
  - database-jvm-ktorm/
  - kafka-utils/
  - multiplatform-test-utils/

Each module contains its own build.gradle.kts and, where applicable, a README.MD.


## Running tests
- Ensure Docker is running for modules that rely on Testcontainers (Kafka/Postgres):
  - libs/database-jvm-jooq tests spin up Postgres (postgres:15) via Testcontainers
  - libs/kafka-utils tests spin up Kafka (apache/kafka:3.7.1) via Testcontainers
- Run:
  ./gradlew test

If Docker isn’t available, you can run only modules that don’t require it, for example:
 `./gradlew :libs:common:test :libs:multiplatform-test-utils:test`


## Development notes
- The repo uses Dokka V2 mode (see gradle.properties) and includes module README in Dokka output.
- Publications are signed and targeted at the Sonatype Central Portal via com.vanniktech.maven.publish.
- Kotlin Multiplatform is configured in libs/common and libs/multiplatform-test-utils; other modules are JVM-only.


## Contributing
PRs and issues are welcome. Please run ./gradlew build locally before opening a PR and include tests where appropriate.

