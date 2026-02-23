// Globální nastavení Gradle projektu
// Konfigurační soubor pro celý projekt, ne pro konkrétní modul

pluginManagement {
    // Repozitáře pro Gradle pluginy
    repositories {
        google {
            // Omezení obsahu repozitáře (zrychlení a bezpečnější resolving)
            content {
                // Povolit jen skupiny, které typicky obsahují Android/Kotlin pluginy
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        // Další zdroj pro pluginy a knihovny
        mavenCentral()
        // Oficiální portál pro Gradle pluginy
        gradlePluginPortal()
    }
}


plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}


dependencyResolutionManagement {
    // Zabrání tomu, aby si jednotlivé moduly definovaly vlastní repositories
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

// Název kořenového (root) projektu v Gradle (zobrazuje se i v Android Studiu)
rootProject.name = "Application"

// Seznam modulů, které patří do projektu
include(":app")
