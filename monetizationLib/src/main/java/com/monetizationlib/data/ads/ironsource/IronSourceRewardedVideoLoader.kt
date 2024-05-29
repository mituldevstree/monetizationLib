package com.monetizationlib.data.ads.ironsource

import android.os.Handler
import com.google.errorprone.annotations.Keep
import com.ironsource.mediationsdk.IronSource
import com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo
import com.ironsource.mediationsdk.logger.IronSourceError
import com.ironsource.mediationsdk.model.Placement
import com.ironsource.mediationsdk.sdk.LevelPlayRewardedVideoManualListener
import com.monetizationlib.data.Monetization
import com.monetizationlib.data.ads.AdProvider
import com.monetizationlib.data.ads.LoaderType
import com.monetizationlib.data.ads.LoaderTypeHelper
import com.monetizationlib.data.ads.MaxRevenueTracker
import com.monetizationlib.data.ads.RewardedVideoLoaderHelper
import com.monetizationlib.data.ads.model.GivvyAd
import com.monetizationlib.data.ads.model.GivvyAdType
import java.util.concurrent.TimeUnit
import kotlin.math.pow

@Keep
object IronSourceRewardedVideoLoader : LevelPlayRewardedVideoManualListener {
    private var hasLoadedAd = false
    private var adInfo: AdInfo? = null
    private var retryAttempt = 0

    fun showVideo(): GivvyAd {
        if (hasLoadedAd && adInfo != null) {
            LoaderTypeHelper.latestLoaderUsedType = LoaderType.IRONSOURCE
            IronSource.showRewardedVideo()
            return GivvyAd(adInfo?.adNetwork, isShown = true, GivvyAdType.Video)
        }
        return GivvyAd(givvyAdType = GivvyAdType.None)
    }

    fun isVideoLoaded(): Boolean {
        return hasLoadedAd
    }

    fun getAdRevenue(): Double {
        return adInfo?.revenue ?: 0.0
    }

    fun loadRewardedVideo() {
        Monetization.logWtfIfNeeded("IronSourceRewardedVideoLoader load rewarded video ad")
        IronSource.loadRewardedVideo()
    }

    // The Rewarded Video ad view has opened. Your activity will loose focus
    override fun onAdOpened(adInfo: AdInfo) {
        Monetization.onAdShown()
        Monetization.logWtfIfNeeded("IronSourceRewardedVideoLoader ad shown with adInfo = $adInfo")
        Monetization.getActivityOrContext()?.let {
            MaxRevenueTracker.addImpressionIronSourceAdInfo(it, adInfo)
        }
        this.adInfo = null
    }

    // The Rewarded Video ad view is about to be closed. Your activity will regain its focus
    override fun onAdClosed(adInfo: AdInfo) {
        loadRewardedVideo()
        Monetization.logWtfIfNeeded("IronSourceRewardedVideoLoader onAdClosed adInfo = $adInfo")
        RewardedVideoLoaderHelper.notifyObserversClosed()
    }
    // Indicates that the Rewarded video ad was loaded successfully.
    // AdInfo parameter includes information about the loaded ad
    override fun onAdReady(p0: AdInfo?) {
        hasLoadedAd = true
        adInfo = p0

        // Reset retry attempt
        retryAttempt = 0

        Monetization.logWtfIfNeeded("IronSourceRewardedVideoLoader onAdReady adInfo = $adInfo")
        RewardedVideoLoaderHelper.notifyObserversAdLoaded()
    }


    // Invoked when the rewarded video failed to load
    override fun onAdLoadFailed(p0: IronSourceError?) {
        adInfo = null
        hasLoadedAd = false
        Monetization.logWtfIfNeeded("IronSourceRewardedVideoLoader onAdLoadFailed with error = $p0")
        RewardedVideoLoaderHelper.notifyObserversOnAdFailed(AdProvider.IRONSOURCE)
        retryAttempt++
        val delayMillis: Long = TimeUnit.SECONDS.toMillis(
            2.0.pow(6.coerceAtMost(retryAttempt).toDouble())
                .toLong()
        )
        Handler().postDelayed(Runnable { loadRewardedVideo() }, delayMillis)
    }

    // The user completed to watch the video, and should be rewarded.
    // The placement parameter will include the reward data.
    // When using server-to-server callbacks, you may ignore this event and wait for the ironSource server callback
    override fun onAdRewarded(placement: Placement, adInfo: AdInfo) {
        Monetization.logWtfIfNeeded("IronSourceRewardedVideoLoader onAdRewarded placement = $placement && adInfo = $adInfo")
        RewardedVideoLoaderHelper.notifyObserversRewarded()
    }

    // The rewarded video ad was failed to show
    override fun onAdShowFailed(error: IronSourceError, adInfo: AdInfo) {
        Monetization.logWtfIfNeeded("IronSourceRewardedVideoLoader onAdShowFailed error = $error && adInfo = $adInfo")
        Monetization.onAdFailedToShow("IronSourceRewardedVideoLoader onAdShowFailed", error.errorCode)
        IronSourceRewardedVideoLoader.adInfo = null

        if (Monetization.monetizationConfig?.shouldForceSentImpressions == true) {
            Monetization.showBestAd()
        } else {
            Monetization.showBestVideoAd()
        }

        loadRewardedVideo()
    }

    // Invoked when the video ad was clicked.
    // This callback is not supported by all networks, and we recommend using it
    // only if it's supported by all networks you included in your build
    override fun onAdClicked(placement: Placement, adInfo: AdInfo) {
        Monetization.logWtfIfNeeded("IronSourceRewardedVideoLoader onClick")
    }

    override fun toString(): String {
        return "IronSourceRewardedVideoLoader"
    }
}
