package com.monetizationlib.data.ads

import android.app.Activity
import android.content.Context
import com.applovin.sdk.AppLovinMediationProvider
import com.applovin.sdk.AppLovinSdk
import com.applovin.sdk.AppLovinSdkInitializationConfiguration
import com.applovin.sdk.AppLovinSdkSettings
import com.applovin.sdk.AppLovinTargetingData
import com.monetizationlib.data.Monetization


class AppLovinAdsInitializerManager private constructor(private var context: Context?) {
    var shouldShowConsent = false
    var hasInitedAds = false

    fun initialize(
        baseActivity: Activity,
        userId: String,
        showConsent: (needToShowConsent:Boolean) -> Unit,
        testDevicesIds: ArrayList<String> = arrayListOf(),
    ) {
        val applovinSdkKey = Monetization.monetizationConfig?.applovinSDKKey
            ?: "tBHz0TZ1CJUndEKBE70f-MA3ynGpt1aObO0ve0QVTBnNn7w74kQRU6tNEzMv5yKGrufQbTJSfkfhHzQouPSVne"

        Monetization.logWtfIfNeeded("AdsInitializerManager initialize method is called successfully with sdkKey = $applovinSdkKey")
        val config = Monetization.monetizationConfig?.adConfig

        val targetingData = AppLovinTargetingData.builder()
            .setEmail(Monetization.userEmail)
//            .setKeywords(mutableListOf("casino", "money", "earn", "cash", "earning", "app", "shoes", "money", "earn"))
//            .setInterests(mutableListOf("casino", "money", "earn", "cash", "earning", "app", "shoes", "money", "earn"))
            .build()

        val initConfig = AppLovinSdkInitializationConfiguration.builder(applovinSdkKey, context)
            .setMediationProvider(AppLovinMediationProvider.MAX)
            .configureSettings { settings: AppLovinSdkSettings ->
                settings.isMuted = Monetization.shouldMuteAds
                settings.userIdentifier = userId
                settings.setVerboseLogging(Monetization.shouldPrintLogs)
            }
            .setTargetingData(targetingData)
//            .setAdUnitIds(config?.getListOfAllAdUnits())
            .setTestDeviceAdvertisingIds(testDevicesIds)
            .build()
        Monetization.logWtfIfNeeded("AdsInitializerManager initialize method is called with initConfig = $initConfig")

        AppLovinSdk.getInstance(baseActivity).initialize(initConfig) {
            Monetization.logWtfIfNeeded("AdsInitializerManager initialize is successful")
            Monetization.onAppLovinInitializedSuccessfully()

            // AppLovin SDK is initialized, start loading ads
            if (ConsentHandler.shouldAskForConsentApplovin(baseActivity)) {
                shouldShowConsent = true
                showConsent(true)
            } else {
                initAds(baseActivity)
                showConsent(false)
            }
        }
    }

    fun showAndHandleConsent(
        baseActivity: Activity, success: () -> Unit?, onAdsInit: () -> Unit? = {},
    ): Boolean {
        val shouldShowConsentAccordingToAppLovin =
            ConsentHandler.shouldAskForConsentApplovin(baseActivity)
        Monetization.logWtfIfNeeded("showAndHandleConsent method is called successfully with shouldShowConsent = $shouldShowConsent and shouldShowConsentAccordingToAppLovin = $shouldShowConsentAccordingToAppLovin ")

        if (shouldShowConsent && shouldShowConsentAccordingToAppLovin) {
            ConsentHandler.showConsent(activity = baseActivity) {
                success()
                Monetization.logWtfIfNeeded("showConsentDialog method is called successfully")
                initAds(baseActivity)
                onAdsInit.invoke()
            }
            return true
        } else {
            success()
            Monetization.logWtfIfNeeded("showConsentDialog method is called successfully, init ads")
            initAds(baseActivity)
            onAdsInit.invoke()
        }

        success()
        return false
    }

    fun initAds(baseActivity: Activity) {
        Monetization.logWtfIfNeeded("AppLovin, init ads")

        if (hasInitedAds) {
            return
        }

        hasInitedAds = true
        Monetization.logWtfIfNeeded("Applovin should be ready. AdsInitializerManager initAds method is called successfully")
        MaxWaterfallInterstitialAdsLoader.getInstance()?.initialize(baseActivity)
        MaxInterstitialAdsLoader.getInstance()?.initialize(baseActivity)
        MaxInterstitialTopBidAdsLoader.getInstance()?.initialize(baseActivity)
        MaxInterstitialMidBidAdsLoader.getInstance()?.initialize(baseActivity)
        RewardedVideoLoaderHelper.initialize(baseActivity)

        val defaultLoader = Ads.getMediumAdsLoader()
        defaultLoader?.setForeground(true)
        context?.let { defaultLoader?.setContext(it) }
        defaultLoader?.setActivity(baseActivity)
        defaultLoader?.startInit()
        defaultLoader?.loadNewAds()

        val largeLoader = Ads.getLargeAdsLoader()
        largeLoader?.setForeground(true)
        context?.let { largeLoader?.setContext(it) }
        largeLoader?.setActivity(baseActivity)
        largeLoader?.startInit()
        largeLoader?.loadNewAds()

        val smallLoader = Ads.getSmallAdsLoader()
        smallLoader?.setForeground(true)
        context?.let { smallLoader?.setContext(it) }
        smallLoader?.setActivity(baseActivity)
        smallLoader?.startInit()
        smallLoader?.loadNewAds()

        val additionalAdsLoader = Ads.getAdditionalAdsLoader()
        additionalAdsLoader?.setForeground(true)
        context?.let { additionalAdsLoader?.setContext(it) }
        additionalAdsLoader?.setActivity(baseActivity)
        additionalAdsLoader?.startInit()
        additionalAdsLoader?.loadNewAds()
    }

    companion object {
        private var instance: AppLovinAdsInitializerManager? = null
        fun getInstance(context: Context): AppLovinAdsInitializerManager? {
            if (instance == null) {
                synchronized(AppLovinAdsInitializerManager::class.java) {
                    if (instance == null) {
                        instance = AppLovinAdsInitializerManager(context)
                    }
                }
            }
            return instance
        }

        fun onDestroy() {
            instance?.context = null
            instance?.hasInitedAds = false
            instance = null
        }
    }
}
