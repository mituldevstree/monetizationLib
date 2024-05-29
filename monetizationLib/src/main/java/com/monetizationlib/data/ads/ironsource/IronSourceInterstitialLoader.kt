package com.monetizationlib.data.ads.ironsource

import android.os.Handler
import com.google.errorprone.annotations.Keep
import com.ironsource.mediationsdk.IronSource
import com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo
import com.ironsource.mediationsdk.logger.IronSourceError
import com.ironsource.mediationsdk.sdk.LevelPlayInterstitialListener
import com.monetizationlib.data.Monetization
import com.monetizationlib.data.ads.LoaderType
import com.monetizationlib.data.ads.LoaderTypeHelper
import com.monetizationlib.data.ads.MaxRevenueTracker
import com.monetizationlib.data.ads.RewardedVideoLoaderHelper
import com.monetizationlib.data.ads.model.GivvyAd
import com.monetizationlib.data.ads.model.GivvyAdType
import java.util.concurrent.TimeUnit
import kotlin.math.pow

@Keep
object IronSourceInterstitialLoader : LevelPlayInterstitialListener {
    private var hasLoadedAd = false
    private var adInfo: AdInfo? = null

    //TODO retryAttempt set to 10 by backend
    private var retryAttempt = 0

    fun showInterstitial(): GivvyAd {
        if (hasLoadedAd && adInfo != null) {
            LoaderTypeHelper.latestLoaderUsedType = LoaderType.IRONSOURCE
            IronSource.showInterstitial()
            return GivvyAd(adInfo?.adNetwork, isShown = true, GivvyAdType.Interstitial)
        }
        return GivvyAd(givvyAdType = GivvyAdType.None)
    }

    fun isInterstitialLoaded(): Boolean {
        return hasLoadedAd
    }

    fun getAdRevenue(): Double {
        return adInfo?.revenue ?: 0.0
    }

    fun loadInterstitial() {
        Monetization.logWtfIfNeeded("IronSourceInterstitialLoader load Interstitial ad")
        IronSource.loadInterstitial()
    }

    // Invoked when the interstitial ad was loaded successfully.
    // AdInfo parameter includes information about the loaded ad
    override fun onAdReady(adInfo: AdInfo) {
        hasLoadedAd = true
        IronSourceInterstitialLoader.adInfo = adInfo

        // Reset retry attempt
        retryAttempt = 0

        Monetization.logWtfIfNeeded("IronSourceInterstitialLoader onAdAvailable adInfo = $adInfo")
        RewardedVideoLoaderHelper.notifyObserversAdLoaded()
    }

    // Indicates that the ad failed to be loaded
    override fun onAdLoadFailed(p0: IronSourceError?) {
        adInfo = null
        hasLoadedAd = false
        Monetization.logWtfIfNeeded("IronSourceInterstitialLoader onAdLoadFailed error = $p0")

        retryAttempt++
        val delayMillis: Long = TimeUnit.SECONDS.toMillis(
            2.0.pow(6.coerceAtMost(retryAttempt).toDouble())
                .toLong()
        )
        Handler().postDelayed(Runnable { loadInterstitial() }, delayMillis)
    }

    // Invoked when the Interstitial Ad Unit has opened, and user left the application screen.
    // This is the impression indication.
    override fun onAdOpened(adInfo: AdInfo) {
        MaxRevenueTracker.isInAd = true
        Monetization.onAdShown()
        Monetization.logWtfIfNeeded("IronSourceInterstitialLoader onAdOpened with adInfo = $adInfo")
        Monetization.getActivityOrContext()?.let {
            MaxRevenueTracker.addImpressionIronSourceAdInfo(it, adInfo)
        }
        this.adInfo = null
    }

    // Invoked when the interstitial ad closed and the user went back to the application screen.
    override fun onAdClosed(adInfo: AdInfo) {
        MaxRevenueTracker.isInAd = false
        Monetization.logWtfIfNeeded("IronSourceInterstitialLoader onAdClosed adInfo = $adInfo")
        Monetization.onInterstitialAdHidden()
        loadInterstitial()
    }

    // Invoked before the interstitial ad was opened, and before the InterstitialOnAdOpenedEvent is reported.
    // This callback is not supported by all networks, and we recommend using it only if
    // it's supported by all networks you included in your build.
    override fun onAdShowSucceeded(p0: AdInfo?) {
        Monetization.logWtfIfNeeded("IronSourceInterstitialLoader onAdShowSucceeded adInfo = $adInfo")
    }

    // Invoked when the ad failed to show
    override fun onAdShowFailed(error: IronSourceError, adInfo: AdInfo) {
        Monetization.logWtfIfNeeded("IronSourceInterstitialLoader onAdShowFailed error = $error && adInfo = $adInfo")
        Monetization.onAdFailedToShow(
            "IronSourceInterstitialLoader onAdShowFailed",
            error.errorCode
        )

        IronSourceInterstitialLoader.adInfo = null

        if (Monetization.monetizationConfig?.shouldForceSentImpressions == true) {
            Monetization.showBestAd()
        } else {
            Monetization.showBestVideoAd()
        }

        loadInterstitial()
    }

    // Invoked when end user clicked on the interstitial ad
    override fun onAdClicked(adInfo: AdInfo) {
        Monetization.logWtfIfNeeded("IronSourceInterstitialLoader onAdClicked with $adInfo")
    }

    override fun toString(): String {
        return "IronSourceInterstitialLoader"
    }
}
