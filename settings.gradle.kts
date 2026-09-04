pluginManagement {
    includeBuild("build-logic")
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
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        versionCatalogs {
            create("moduleNamespace") {
                from(files("gradle/moduleNamespace.versions.toml"))
            }
        }
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Tradebot"
include(":app")
include(":common:router")
include(":core:common")
include(":core:dependency")
include(":core:network")
include(":core:uikit")
include(":service:network")
include(":core:utils")
include(":feature:splashScreen:presentation")
include(":feature:splashScreen:router")
include(":feature:main:route")
include(":feature:main:presentation")
include(":feature:dashboard:router")
include(":feature:dashboard:presentation")
include(":feature:positions:router")
include(":feature:strategy:router")
include(":feature:risk:router")
include(":feature:more:router")
include(":feature:more:presentation")
include(":feature:more:api")
include(":feature:more:domain")
include(":feature:more:data")
include(":feature:positions:presentation")
include(":feature:positions:domain")
include(":feature:positions:data")
include(":feature:positions:api")
include(":feature:instruments:router")
include(":feature:instruments:presentation")
include(":feature:instruments:domain")
include(":feature:instruments:data")
include(":feature:instruments:api")
include(":feature:risk:presentation")
include(":feature:risk:api")
include(":feature:risk:data")
include(":feature:risk:domain")
include(":feature:strategy:presentation")
include(":feature:strategy:domain")
include(":feature:strategy:data")
include(":feature:strategy:api")
include(":feature:dashboard:domain")
include(":feature:dashboard:data")
include(":feature:dashboard:api")
include(":feature:notifications:api")
include(":feature:notifications:domain")
include(":feature:notifications:data")
