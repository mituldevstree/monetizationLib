plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.kotlinKapt)
    alias(libs.plugins.parcelize)
    alias(libs.plugins.navigation.safeargs)
//    id("com.google.gms.google-services")
}

android {
    namespace = "com.monetizationlib.data"
    compileSdk = 34

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            buildConfigField("String", "API_URL", "\"https://givvy-general-config.herokuapp.com\"")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            buildConfigField("String", "API_URL", "\"https://givvy-general-config.herokuapp.com\"")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    android.buildFeatures.buildConfig = true
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    implementation(libs.gson)
    implementation(libs.sdp.android)
    implementation(libs.ssp.android)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)
    implementation(libs.okio)

    implementation(libs.androidx.swiperefreshlayout)
    implementation(libs.lottie)
    implementation(libs.slidetoact)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.lifecycle.extensions)

    implementation (libs.androidx.lifecycle.viewmodel.ktx)
    implementation (libs.androidx.lifecycle.livedata.ktx)
    implementation (libs.androidx.lifecycle.extensions)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.fragment.ktx)
    implementation (libs.kotlinx.coroutines.android)
    implementation (libs.gson)

// Mintegral
    implementation(libs.mbbanner)
    implementation(libs.mbbid)
    implementation(libs.newinterstitial)
    //TODO getting build so make commented for now
//    implementation (libs.reward)

    //InMobi
    implementation(libs.inmobi.ads.kotlin)

    // Recommended: Add the Google Analytics SDK.
    implementation(libs.firebase.analytics)
//Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.15.1")
    implementation("com.github.bumptech.glide:okhttp3-integration:4.14.2")


    //ads
    //AppLovin
    implementation(libs.applovin.sdk)
    implementation("com.applovin.mediation:google-ad-manager-adapter:23.1.0.0")
    implementation("com.applovin.mediation:adcolony-adapter:4.8.0.4")
//    implementation("com.google.android.gms:play-services-base:18.4.0")
    implementation("com.applovin.mediation:fyber-adapter:8.2.7.1")
    implementation("com.applovin.mediation:ironsource-adapter:8.1.0.0.0")
    implementation("com.applovin.mediation:mintegral-adapter:16.7.41.0")

    implementation("com.applovin.mediation:unityads-adapter:4.11.3.0")
    implementation("com.applovin.mediation:vungle-adapter:7.3.2.1")
    implementation("com.applovin.mediation:bytedance-adapter:5.9.0.6.0")
    implementation("com.applovin.mediation:smaato-adapter:22.6.1.0")
    implementation("com.applovin.mediation:inmobi-adapter:10.6.7.0")
    implementation("com.applovin.mediation:chartboost-adapter:9.7.0.0")
    implementation("com.applovin.mediation:mytarget-adapter:5.20.1.0")
    implementation("com.applovin.mediation:ogury-presage-adapter:5.7.0.0")
    implementation("com.squareup.picasso:picasso:2.71828")

    implementation("com.applovin.mediation:tapjoy-adapter:13.2.0.0") {
        exclude(group = "com.google.android.gms") //by group
    }
    implementation("com.applovin.mediation:facebook-adapter:6.17.0.0")
    implementation("androidx.fragment:fragment-ktx:1.7.1")
    implementation("com.applovin.mediation:hyprmx-adapter:6.2.0.2")
    implementation("com.startapp:applovin-mediation:1.1.7")
    implementation("com.mobilefuse.sdk:mobilefuse-adapter-applovin:1.7.4.0")
    implementation("com.applovin.mediation:bidmachine-adapter:2.7.0.1")
    implementation("com.applovin.mediation:fyber-adapter:8.2.7.1")

    //noinspection GradleDynamicVersion
    implementation("com.tappx.sdk.android:tappx-sdk:4.1.1")
    //noinspection GradleDynamicVersion
    implementation("com.applovin.mediation:tappx-adapter:3+")

    // Pangle
    implementation("com.pangle.global:ads-sdk:5.9.0.6")
    implementation("com.github.MFlisar.MaterialDialogs:core:0.8.5")
    implementation("com.github.MFlisar.MaterialDialogs:dialogs-gdpr:0.8.5")
    implementation("com.github.MFlisar.MaterialDialogs:extensions-fragment-dialog:0.8.5")

    //FairBid
    implementation("com.fyber:fairbid-sdk:3.49.1")

    // AdColony
    implementation("com.adcolony:sdk:4.8.0")

    // IronSource
    implementation("com.ironsource.sdk:mediationsdk:8.1.0")
    // Add Applovin Network
    implementation("com.ironsource.adapters:applovinadapter:4.3.43")
    // Add BidMachine Network
    implementation("com.ironsource.adapters:bidmachineadapter:4.3.6")
    implementation("io.bidmachine:ads:2.7.0")
    // Add Fyber Network (Adapter only)
    implementation("com.ironsource.adapters:fyberadapter:4.3.30")
    implementation("com.fyber:marketplace-sdk:8.2.7")
    // Add Facebook Network
    implementation("com.ironsource.adapters:facebookadapter:4.3.46")
    implementation("com.facebook.android:audience-network-sdk:6.17.0")
    // Add HyprMX Network
    implementation("com.ironsource.adapters:hyprmxadapter:4.3.8")
    implementation("com.hyprmx.android:HyprMX-SDK:6.4.0")
    // Add Vungle Network
    implementation("com.ironsource.adapters:vungleadapter:4.3.7")
    implementation("com.vungle:vungle-ads:7.1.0")
    //Add Mintegral
    implementation("com.ironsource.adapters:mintegraladapter:4.3.25")
    // Add myTarget Network
    implementation("com.ironsource.adapters:mytargetadapter:4.1.19")
    implementation("com.my.target:mytarget-sdk:5.20.1")
    implementation("com.google.android.exoplayer:exoplayer:2.19.1")
    // Add Pangle Network
    implementation("com.ironsource.adapters:pangleadapter:4.3.26")
    // Add Smaato Network
    implementation("com.ironsource.adapters:smaatoadapter:4.3.10")
    implementation("com.smaato.android.sdk:smaato-sdk-banner:22.6.1")
    implementation("com.smaato.android.sdk:smaato-sdk-in-app-bidding:22.6.1")
    // Add InMobi Network
    implementation("com.ironsource.adapters:inmobiadapter:4.3.24")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}