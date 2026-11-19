// Repositorios donde Gradle busca los plugins de compilación.
pluginManagement { repositories { google(); mavenCentral(); gradlePluginPortal() } }
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.10.0"
}
// Centraliza los repositorios de librerías; evita declararlos de nuevo en cada módulo.
dependencyResolutionManagement { repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS); repositories { google(); mavenCentral() } }
rootProject.name = "PokedexEA3"
// Registra app como el módulo Android que se puede compilar y ejecutar.
include(":app")
