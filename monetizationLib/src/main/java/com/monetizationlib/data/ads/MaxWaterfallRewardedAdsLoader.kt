package com.monetizationlib.data.ads


import android.app.Activity
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxError
import com.applovin.mediation.MaxReward
import com.applovin.mediation.MaxRewardedAdListener
import com.applovin.mediation.ads.MaxRewardedAd
import com.google.errorprone.annotations.Keep
import com.monetizationlib.data.Monetization
import com.monetizationlib.data.ads.model.GivvyAd
import com.monetizationlib.data.ads.model.GivvyAdType


@Keep
object MaxWaterfallRewardedAdsLoader : RewardedVideosLoader(), MaxRewardedAdListener {

    private var rewardedAd: MaxRewardedAd? = null
    private var rewardedAdFetched: MaxAd? = null
    private var adRevenue: Double = 0.0
    private var networkName: String = "unknown"

    fun hasEverLoadedAd() = rewardedAdFetched != null

    fun initialize() {
        Monetization.logWtfIfNeeded("MaxWaterfallRewardedAdsLoader initialize method is called successfully")

        val adUnit = when (Monetization.adConfig?.waterfallRewardedAdUnit?.isNullOrEmpty()) {
            false -> Monetization.adConfig?.waterfallRewardedAdUnit
            else -> Monetization.adConfig?.rewardedAdUnit
        }

        rewardedAd =
            MaxRewardedAd.getInstance(adUnit, Monetization.getActivityOrContext() as Activity);
        rewardedAd?.setListener(this);
        rewardedAd?.setRevenueListener(
            Monetization.getActivityOrContext()?.let { MaxRevenueTracker.getMaxRevenueTracker(it) })
        loadAd()
    }


    fun hasLoadedAd(): Boolean {
        return rewardedAd?.isReady == true
    }

    fun getLoadedAd(): MaxRewardedAd? {
        if(rewardedAd?.isReady == false) {
            onAdLoadedButNotReady()
        }
        return rewardedAd
    }


    fun getRevenue(): Double {
        return adRevenue
    }

    override fun init(activity: Activity?) {
        activity?.let { initialize() }
    }

    override fun loadAd() {
        rewardedAd?.loadAd();
        Monetization.logWtfIfNeeded("MaxWaterfallRewardedAdsLoader loadAd method is called successfully")
    }

    override fun getTag(): String {
        return AdProvider.MAX_WATERFALL.getProviderString()
    }

    override fun playAd(): GivvyAd {
        Monetization.logWtfIfNeeded("MaxWaterfallRewardedAdsLoader playAd method is called successfully with rewardedAd = rewardedAd?.isReady = ${rewardedAd?.isReady}")
        if (rewardedAd?.isReady == true) {
            LoaderTypeHelper.latestLoaderUsedType = LoaderType.APPLOVIN
            rewardedAd?.showAd()
            return GivvyAd(providerName = networkName, true, GivvyAdType.Video)
        } else {
            return GivvyAd(providerName = networkName, false, GivvyAdType.Video)

        }
    }

    override fun onAdLoaded(ad: MaxAd) {
        MaxRevenueTracker.onAdFetched(ad, true)
        Monetization.logWtfIfNeeded("MaxWaterfallRewardedAdsLoader onAdLoaded method is called successfully with ad = $ad, ad.networkName = ${ad.networkName}")
        // Rewarded ad is ready to be shown. rewardedAd.isReady() will now return 'true'
        rewardedVideoListener?.onAdLoaded()
        rewardedAdFetched = ad
        adRevenue = rewardedAdFetched?.revenue ?: 0.0
        networkName = ad.networkName
    }

    override fun onAdLoadFailed(adUnitId: String, error: MaxError) {
        Monetization.logWtfIfNeeded("MaxWaterfallRewardedAdsLoader onAdLoadFailed method is called successfully with error = $error")
//        rewardedVideoListener?.onAdFailed(AdProvider.MAX_WATERFALL)
        RewardedVideoLoaderHelper.notifyObserversOnAdFailed(AdProvider.MAX_WATERFALL)
    }

    override fun onAdDisplayed(ad: MaxAd) {
        MaxRevenueTracker.isInAd = true
        Monetization.onAdShown()
    }

    override fun onAdHidden(ad: MaxAd) {
        MaxRevenueTracker.isInAd = false
        rewardedAdFetched = null

        rewardedAd?.loadAd();
        rewardedVideoListener?.onAdClosed()
    }

    override fun onAdClicked(ad: MaxAd) {
        if (MaxRevenueTracker.currentImpressions.size > 0) {
            val ad = MaxRevenueTracker.currentImpressions.lastOrNull {
                val adUnitFormat = it.adUnitFormat ?: ""
                adUnitFormat.lowercase() == "rewarded"
            }

            if (ad != null) {
                ad.hasBeenClicked = true
                MaxRevenueTracker.isInAd = false
                Monetization.logWtfIfNeeded("sendImpressionData MaxWaterfallRewardedAdsLoader onAdClicked")
                MaxRevenueTracker.sendImpressionData()
            }
        }
    }

    override fun onAdDisplayFailed(ad: MaxAd, error: MaxError) {
        Monetization.logWtfIfNeeded("MaxWaterfallRewardedAdsLoader onAdDisplayFailed method is called successfully with error = $error")
        Monetization.onAdFailedToShow(error?.toString() ?: "", error.code)

        adRevenue = 0.0
        networkName = "unknown"
        if (error?.code == -23 || error?.message?.contains("fullscreen", true) == true) {
            rewardedAd?.destroy()
            rewardedAd = null
            adRevenue = 0.0
            networkName = "unknown"
            initialize()
        } else {
            rewardedAd?.loadAd();
        }

        if (Monetization.monetizationConfig?.shouldForceSentImpressions == true) {
            Monetization.showBestAd()
        } else {
            Monetization.showBestVideoAd()
        }
    }

    override fun onRewardedVideoStarted(ad: MaxAd) {
    }

    override fun onRewardedVideoCompleted(ad: MaxAd) {

    }

    override fun onUserRewarded(ad: MaxAd, reward: MaxReward) {
        Monetization.logWtfIfNeeded("MaxWaterfallRewardedAdsLoader onUserRewarded method is called successfully with reward = $reward")
        rewardedVideoListener?.onRewarded()
    }

    fun onAdLoadedButNotReady() {
        Monetization.logWtfIfNeeded("MaxWaterfallRewardedAdsLoader onAdLoadedButNotReady called with interstitialAd?.isReady = ${rewardedAd?.isReady} && interstitialAd = $rewardedAd")
        if (rewardedAd != null && rewardedAd?.isReady != true) {
            Monetization.logWtfIfNeeded("MaxWaterfallRewardedAdsLoader onAdLoadedButNotReady")
            rewardedAd?.destroy()
            rewardedAd = null
            adRevenue = 0.0
            networkName = "unknown"
            initialize()
        }
    }

    fun hasLoadedAdReady(): Boolean? {
        return rewardedAd?.isReady
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
        return "MaxWaterfallRewardedAdsLoader"
    }
}
