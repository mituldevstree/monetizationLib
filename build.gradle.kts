// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.kotlinKapt) apply false
    alias(libs.plugins.parcelize) apply false
    alias(libs.plugins.navigation.safeargs) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.fyber.fairbid.sdk) apply false
//    id("com.google.gms.google-services") version "4.3.15" apply false
}