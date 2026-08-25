pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"

    id("com.autonomousapps.build-health") version "3.18.0"
    id("com.android.application") version "9.3.2" apply false
    id("com.android.library") version "9.3.2" apply false
    id("org.jetbrains.kotlin.android") version "2.4.10" apply false

}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "avito2026autumn"
include(":app")

include(":core:design")
include(":core:data:notes")
include(":core:data:tasks")
include(":core:data:settings")
include(":core:data:ai")

include(":feat:notes")
include(":feat:tasks")
include(":feat:note_editor")
include(":feat:settings")
include(":core:navigation")


