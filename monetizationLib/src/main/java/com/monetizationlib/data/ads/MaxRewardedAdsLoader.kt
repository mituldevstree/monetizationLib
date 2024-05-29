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
object MaxRewardedAdsLoader : RewardedVideosLoader(), MaxRewardedAdListener {

    private var rewardedAd: MaxRewardedAd? = null
    private var rewardedAdFetched: MaxAd? = null
    private var adRevenue: Double = 0.0
    private var networkName: String = "unknown"

    fun initialize() {
        Monetization.logWtfIfNeeded("MaxRewardedAdsLoader initialize method is called successfully")
        rewardedAd =
            MaxRewardedAd.getInstance(
                Monetization.adConfig?.rewardedAdUnit,
                Monetization.getActivityOrContext() as Activity?
            );
        rewardedAd?.setListener(this);
        rewardedAd?.setRevenueListener(Monetization.getActivityOrContext()
            ?.let { MaxRevenueTracker.getMaxRevenueTracker(it) })
    }

    override fun init(activity: Activity?) {
        activity?.let { initialize() }
    }

    override fun loadAd() {
        rewardedAd?.loadAd();
        Monetization.logWtfIfNeeded("MaxRewardedAdsLoader loadAd method is called successfully")
    }

    override fun getTag(): String {
        return AdProvider.MAX.getProviderString()
    }

    override fun playAd(): GivvyAd {
        Monetization.logWtfIfNeeded("MaxRewardedAdsLoader playAd method is called successfully with rewardedAd?.isReady = ${rewardedAd?.isReady}")
        if (rewardedAd?.isReady == true) {
            LoaderTypeHelper.latestLoaderUsedType = LoaderType.APPLOVIN
            rewardedAd?.showAd();
            return GivvyAd(providerName = networkName, true, GivvyAdType.Video)
        } else {
            return GivvyAd(providerName = networkName, false, GivvyAdType.Video)
        }
    }

    override fun onAdLoaded(ad: MaxAd) {
        MaxRevenueTracker.onAdFetched(ad, false)
        Monetization.logWtfIfNeeded("MaxRewardedAdsLoader onAdLoaded method is called successfully with ad = $ad, ad.networkName = ${ad.networkName}")
        // Rewarded ad is ready to be shown. rewardedAd.isReady() will now return 'true'
        rewardedVideoListener?.onAdLoaded()
        rewardedAdFetched = ad
        adRevenue = rewardedAdFetched?.revenue ?: 0.0
        networkName = ad.networkName
    }

    override fun onAdLoadFailed(adUnitId: String, error: MaxError) {
        Monetization.logWtfIfNeeded("MaxRewardedAdsLoader onAdLoaded method is called successfully with error = $error")
//        rewardedVideoListener?.onAdFailed(AdProvider.MAX)
        RewardedVideoLoaderHelper.notifyObserversOnAdFailed(AdProvider.MAX)
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

    fun getRevenue(): Double {
        return adRevenue
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
                Monetization.logWtfIfNeeded("sendImpressionData MaxRewardedAdsLoader onAdClicked")
                MaxRevenueTracker.sendImpressionData()
            }
        }
    }

    override fun onAdDisplayFailed(ad: MaxAd, error: MaxError) {
        Monetization.logWtfIfNeeded("MaxRewardedAdsLoader onAdDisplayFailed method is called successfully with error = $error")
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

    fun onAdLoadedButNotReady() {
        Monetization.logWtfIfNeeded("MaxRewardedAdsLoader onAdLoadedButNotReady called with rewardedAd?.isReady = ${rewardedAd?.isReady} && rewardedAd = $rewardedAd")
        if (rewardedAd != null && rewardedAd?.isReady != true) {
            Monetization.logWtfIfNeeded("MaxRewardedAdsLoader onAdLoadedButNotReady")
            rewardedAd?.destroy()
            rewardedAd = null
            adRevenue = 0.0
            networkName = "unknown"
            initialize()
        }
    }

    fun hasEverLoadedAd() = rewardedAdFetched != null

    fun hasLoadedAd(): Boolean {
        return rewardedAd?.isReady == true
    }

    fun getLoadedAd(): MaxRewardedAd? {
        if(rewardedAd?.isReady == false) {
            onAdLoadedButNotReady()
        }
        return rewardedAd
    }

    override fun onUserRewarded(ad: MaxAd, reward: MaxReward) {
        rewardedVideoListener?.onRewarded()
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
        return "MaxRewardedAdsLoader"
    }
}
