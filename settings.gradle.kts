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
        maven(url = "https://artifacts.applovin.com/android")
        maven(
            url = "https://jitpack.io"

        )
        maven(url = "https://jitpack.io") {
            val authToken: String by settings
            credentials { username = authToken }
        }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://artifacts.applovin.com/android") }
        maven(url = "https://jitpack.io")
        maven(
            url = "https://bitbucket.org/adscend/androidsdk/raw/master/"
        )
        maven(url = "https://android-sdk.is.com/")
        maven(url = "https://mvnrepository.com/artifact/com.inmobi.monetization/inmobi-ads")
        maven(url = "https://hyprmx.jfrog.io/artifactory/hyprmx")

        maven(url = "https://maven.ogury.co")
        maven(url = "https://artifactory.bidmachine.io/bidmachine")
        maven(url = "https://artifact.bytedance.com/repository/pangle")
        maven(url = "https://s3.amazonaws.com/smaato-sdk-releases/")
        maven(url = "https://cboost.jfrog.io/artifactory/chartboost-ads/")
        maven(url = "https://maven.ogury.co")
        maven(url = "https://dl-maven-android.mintegral.com/repository/mbridge_android_sdk_oversea")
    }
}

rootProject.name = "MonetizationLib"
include(":app")
include(":monetizationLib")
