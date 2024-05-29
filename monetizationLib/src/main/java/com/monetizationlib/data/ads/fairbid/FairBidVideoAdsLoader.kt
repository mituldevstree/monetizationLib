package com.monetizationlib.data.ads.fairbid

import android.app.Activity
import android.util.Log
import com.fyber.fairbid.ads.ImpressionData
import com.fyber.fairbid.ads.Rewarded
import com.fyber.fairbid.ads.rewarded.RewardedListener
import com.google.errorprone.annotations.Keep
import com.monetizationlib.data.FastCashOutAdFormat
import com.monetizationlib.data.Monetization
import com.monetizationlib.data.ads.*
import com.monetizationlib.data.ads.model.GivvyAd
import com.monetizationlib.data.ads.model.GivvyAdType

@Keep
class FairBidVideoAdsLoader : RewardedListener {
    private var placementId: String = ""
    private var netPayout = 0.0
    private var isInitialized = false
    private var impressionData: ImpressionData? = null

    companion object {
        private var instance: FairBidVideoAdsLoader? = null
        fun getInstance(): FairBidVideoAdsLoader? {
            if (instance == null) {
                synchronized(FairBidVideoAdsLoader::class.java) {
                    if (instance == null) {
                        instance = FairBidVideoAdsLoader()
                    }
                }
            }
            return instance
        }

        fun onDestroy() {
            instance = null
        }

        override fun toString(): String {
            return "FairBidVideoAdsLoader"
        }
    }

    fun initialize(placementId: String) {
        if(isInitialized) return
        this.placementId = placementId
        Rewarded.setRewardedListener(this)
        Rewarded.request(this.placementId)
        isInitialized = true
    }

    fun isRewardedVideoAvailable(): Boolean {
        return Rewarded.isAvailable(placementId)
    }

    fun getRewardedVideoImpressionData(): ImpressionData? {
        return impressionData
    }

    fun getNetPayout(): Double? {
        return netPayout
    }

    fun hasLoadedAd(): Boolean {
        return Rewarded.isAvailable(placementId)
    }

    fun showAd(): GivvyAd {
        Monetization.logWtfIfNeeded("FairBidVideoAdsLoader showAd called")
        if (Rewarded.isAvailable(placementId)) {
            Monetization.logWtfIfNeeded("FairBidVideoAdsLoader showAd called with isAvailable")
            (Monetization.getActivityOrContext() as? Activity)?.let {
                LoaderTypeHelper.latestLoaderUsedType = LoaderType.FAIRBID
                Rewarded.show(placementId, it)
                return GivvyAd(providerName = getNetworkName(), true, GivvyAdType.Video)
            }
            return GivvyAd(providerName = getNetworkName(), false, GivvyAdType.Video)
        } else {
            Monetization.logWtfIfNeeded("FairBidVideoAdsLoader showAd called without isAvailable")
            Rewarded.request(placementId)
            return GivvyAd(providerName = getNetworkName(), false, GivvyAdType.Video)
        }
    }

    override fun onShow(p0: String, p1: ImpressionData) {
        val netPayout: Double = p1.netPayout
        val currency: String = p1.currency
        val priceAccuracy: ImpressionData.PriceAccuracy = p1.priceAccuracy
        val impressionDepth: Int = p1.impressionDepth

        val message = java.lang.String.format(
            "FairBidVideoAdsLoader - Placement %s has been shown with a net payout of %f %s with accuracy: %s and impression depth: %s and %s",
            p0,
            netPayout,
            currency,
            priceAccuracy,
            impressionDepth,
            p1.jsonString
        )

        Monetization.onAdShown()

        Monetization.logWtfIfNeeded(message)
        Monetization.getActivityOrContext()?.let {
            MaxRevenueTracker.addImpressionData(
                it, p1,
                isInterstitial = false,
                isVideo = true,
                isBanner = false
            )
        }

        this.netPayout = 0.0
        this.impressionData = null
    }

    override fun onClick(placementId: String) {
        Monetization.logWtfIfNeeded("FairBidVideoAdsLoader onClick")
    }

    override fun onHide(placementId: String) {
        Monetization.logWtfIfNeeded("FairBidVideoAdsLoader onHide placementId = $placementId")
        RewardedVideoLoaderHelper.notifyObserversClosed()
    }

    override fun onShowFailure(placementId: String, impressionData: ImpressionData) {
        Monetization.logWtfIfNeeded("FairBidVideoAdsLoader onShowFailure placementId = $placementId")
        Monetization.onAdFailedToShow("FairBidVideoAdsLoader onShowFailure", -11111)

        netPayout = 0.0
        this.impressionData = null
        isInitialized = false
        initialize(this.placementId)

        if (Monetization.monetizationConfig?.shouldForceSentImpressions == true){
            Monetization.showBestAd()
        } else {
            Monetization.showBestVideoAd()
        }
    }

    fun getVideoRevenueData(): Double {
        return impressionData?.netPayout ?: 0.0
    }

    override fun onAvailable(placementId: String) {
        impressionData = Rewarded.getImpressionData(this.placementId)
        netPayout = getRewardedVideoImpressionData()?.netPayout ?: 0.0
        MaxRevenueTracker.onFairBidAdFetched(
            getRewardedVideoImpressionData(),
            FastCashOutAdFormat.REWARDED
        )
        Monetization.logWtfIfNeeded("FairBidVideoAdsLoader onAvailable placementId = $placementId && netPayout = ${getRewardedVideoImpressionData()?.netPayout} \n impressionData = $impressionData ")
        RewardedVideoLoaderHelper.notifyObserversAdLoaded()
    }

    override fun onUnavailable(placementId: String) {
        Monetization.logWtfIfNeeded("FairBidVideoAdsLoader onUnavailable $placementId")
        RewardedVideoLoaderHelper.notifyObserversOnAdFailed(AdProvider.FAIRBID)
    }

    override fun onCompletion(placementId: String, userRewarded: Boolean) {
        Monetization.logWtfIfNeeded("FairBidVideoAdsLoader onCompletion placementId = $placementId && userRewarded = userRewarded")
        RewardedVideoLoaderHelper.notifyObserversRewarded()
    }

    override fun onRequestStart(placementId: String, requestId: String) {
        Monetization.logWtfIfNeeded("FairBidVideoAdsLoader onRequestStart with placementId = $placementId && requestId = $requestId")
    }
    private fun getNetworkName(): String? {
        return impressionData?.demandSource

    }

    fun isFacebookAd(): Boolean {
        return getNetworkName()?.contains("meta", true) == true || getNetworkName()?.contains(
            "facebook", true
        ) == true
    }

    override fun toString(): String {
        return "FairBidVideoAdsLoader"
    }
}
