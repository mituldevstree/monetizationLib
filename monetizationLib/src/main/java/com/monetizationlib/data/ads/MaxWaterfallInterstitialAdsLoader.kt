package com.monetizationlib.data.ads

import android.app.Activity
import android.content.Context
import android.os.Handler
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxInterstitialAd
import com.applovin.sdk.AppLovinSdk
import com.google.errorprone.annotations.Keep
import com.monetizationlib.data.Monetization
import com.monetizationlib.data.ads.model.GivvyAd
import com.monetizationlib.data.ads.model.GivvyAdType
import java.util.*
import java.util.concurrent.TimeUnit


@Keep
class MaxWaterfallInterstitialAdsLoader : IInterstitialAdsLoader, MaxAdListener {

    private var interstitialAd: MaxInterstitialAd? = null
    private var retryAttempt = 0
    private var adsShown = 0
    private var timeToLoad: Long = 0
    private var hasLoadedAdInternal: Boolean = false
    private var adRevenue: Double = 0.0
    private var networkName: String = "unknown"

    companion object {
        private var successCallback: () -> Unit = { }

        private var instance: MaxWaterfallInterstitialAdsLoader? = null
        fun getInstance(): MaxWaterfallInterstitialAdsLoader? {
            if (instance == null) {
                synchronized(MaxWaterfallInterstitialAdsLoader::class.java) {
                    if (instance == null) {
                        instance = MaxWaterfallInterstitialAdsLoader()
                    }
                }
            }
            return instance
        }

        fun setLoadCallback(success: () -> Unit) {
            this.successCallback = success
        }

        override fun toString(): String {
            return "MaxWaterfallInterstitialAdsLoader"
        }
    }

    override fun initialize(context: Context) {
        Monetization.logWtfIfNeeded("MaxWaterfallInterstitialAdsLoader initialize method is called successfully")
        val adUnit =
            when (Monetization.adConfig?.waterfallInterstitialAdUnit?.isNullOrEmpty()) {
                false -> Monetization.adConfig?.waterfallInterstitialAdUnit
                else -> Monetization.adConfig?.interstitialAdUnit
            }

        interstitialAd = MaxInterstitialAd(
            adUnit,
            AppLovinSdk.getInstance(Monetization.getActivityOrContext()),
            Monetization.getActivityOrContext() as Activity
        )
        interstitialAd?.setListener(this)
        interstitialAd?.setRevenueListener(Monetization.getActivityOrContext()
            ?.let { MaxRevenueTracker.getMaxRevenueTracker(it) })
        loadAd()
    }

    override fun showAd(): GivvyAd {
        Monetization.logWtfIfNeeded("MaxWaterfallInterstitialAdsLoader showAd method is called successfully with interstitialAd = $interstitialAd && interstitialAd?.isReady == ${interstitialAd?.isReady == true}")
        if (interstitialAd?.isReady == true) {
            LoaderTypeHelper.latestLoaderUsedType = LoaderType.APPLOVIN
            interstitialAd?.showAd()
            return GivvyAd(providerName = networkName, true, GivvyAdType.Interstitial)
        }
        return GivvyAd(providerName = networkName, false, GivvyAdType.Interstitial)
    }

    fun hasLoadedAd() = interstitialAd?.isReady == true

    fun getLoadedAd(): MaxInterstitialAd? {
        if(interstitialAd?.isReady == false) {
            onAdLoadedButNotReady()
        }
        return interstitialAd
    }

    fun hasLoadedAdInternal() = hasLoadedAdInternal

    override fun loadAd() {
        if (adsShown == 0) {
            timeToLoad = Date().time
        }
        hasLoadedAdInternal = false
        interstitialAd?.loadAd()
        Monetization.logWtfIfNeeded("MaxWaterfallInterstitialAdsLoader loadAd method is called successfully with interstitialAd == $interstitialAd")
    }

    override fun adDismissed() {
    }

    override fun onAdLoaded(ad: MaxAd) {
        MaxRevenueTracker.onAdFetched(ad, true)
        adRevenue = ad?.revenue ?: 0.0
        networkName = ad.networkName
        Monetization.logWtfIfNeeded("MaxWaterfallInterstitialAdsLoader onAdLoaded method is called successfully with interstitialAd == $interstitialAd with adRevenue = $adRevenue and AD = $ad, networkName = $networkName")
        hasLoadedAdInternal = true
        if (adsShown == 0) {
            timeToLoad = Date().time - timeToLoad
            ad?.waterfall?.latencyMillis
            Monetization.notifyFirstInterstitialLoaded(
                timeToLoad,
                ad?.waterfall?.latencyMillis ?: -1
            )
        }
        // Interstitial ad is ready to be shown. interstitialAd.isReady() will now return 'true'

        // Reset retry attempt
        retryAttempt = 0;

        successCallback.invoke()
    }

    override fun onAdLoadFailed(adUnitId: String, error: MaxError) {
        Monetization.logWtfIfNeeded("MaxWaterfallInterstitialAdsLoader onAdLoadFailed method is called successfully with error == $error")
        // Interstitial ad failed to load
        // We recommend retrying with exponentially higher delays up to a maximum delay (in this case 64 seconds)
        hasLoadedAdInternal = false
        retryAttempt++
        val delayMillis: Long = TimeUnit.SECONDS.toMillis(
            Math.pow(2.0, Math.min(6, retryAttempt).toDouble())
                .toLong()
        )

        Handler().postDelayed(Runnable { interstitialAd!!.loadAd() }, delayMillis)
    }

    override fun onAdDisplayed(ad: MaxAd) {
        MaxRevenueTracker.isInAd = true
        Monetization.onAdShown()

        adsShown++
        adRevenue = 0.0
    }

    override fun onAdHidden(ad: MaxAd) {
        MaxRevenueTracker.isInAd = false
        networkName = "unknown"

        // Interstitial ad is hidden. Pre-load the next ad
        loadAd()
        Monetization.onInterstitialAdHidden()
    }

    override fun onAdClicked(ad: MaxAd) {
        if (MaxRevenueTracker.currentImpressions.size > 0) {
            val ad = MaxRevenueTracker.currentImpressions.lastOrNull {
                val adUnitFormat = it.adUnitFormat ?: ""
                adUnitFormat.lowercase() == "interstitial"
            }

            if (ad != null) {
                ad.hasBeenClicked = true
                MaxRevenueTracker.isInAd = false
                Monetization.logWtfIfNeeded("sendImpressionData MaxWaterfallInterstitialAdsLoader onAdClicked")
                MaxRevenueTracker.sendImpressionData()
            }
        }
    }

    override fun onAdDisplayFailed(ad: MaxAd, error: MaxError) {
        Monetization.logWtfIfNeeded("MaxWaterfallInterstitialAdsLoader onAdDisplayFailed method is called successfully with error == $error and ad = $ad")
        Monetization.onAdFailedToShow(error?.toString() ?: "", error.code)

        adRevenue = 0.0
        networkName = "unknown"
        if (error?.code == -23 || error?.message?.contains("fullscreen", true) == true) {
            interstitialAd?.destroy()
            interstitialAd = null
            Monetization.getActivityOrContext()?.let { initialize(it) }
        } else {
            loadAd()
        }

        if (Monetization.monetizationConfig?.shouldForceSentImpressions == true) {
            Monetization.showBestAd()
        } else {
            Monetization.showInterstitial()
        }
    }

    fun hasLoadedAdAndNotShowedAny(): Boolean {
        return interstitialAd?.isReady == true && adsShown == 0
    }

    override fun onAdLoadedButNotReady() {
        Monetization.logWtfIfNeeded("MaxWaterfallInterstitialAdsLoader onAdLoadedButNotReady called with interstitialAd?.isReady = ${interstitialAd?.isReady} && interstitialAd = $interstitialAd")
        if (interstitialAd != null && interstitialAd?.isReady != true) {
            Monetization.logWtfIfNeeded("MaxWaterfallInterstitialAdsLoader onAdLoadedButNotReady")
            interstitialAd?.destroy()
            interstitialAd = null
            adRevenue = 0.0
            networkName = "unknown"
            Monetization.getActivityOrContext()?.let { initialize(it) }
        }
    }

    fun getAdRevenue(): Double {
        return adRevenue
    }

    fun getNetworkName(): String {
        return networkName
    }

    fun isFacebookAd(): Boolean {
        return networkName.contains("meta", true) || networkName.contains(
            "facebook", true
        )
    }

    override fun toString(): String {
        return "MaxWaterfallInterstitialAdsLoader"
    }
}
