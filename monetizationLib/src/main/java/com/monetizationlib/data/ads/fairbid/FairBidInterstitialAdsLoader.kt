package com.monetizationlib.data.ads.fairbid

import android.app.Activity
import com.fyber.fairbid.ads.ImpressionData
import com.fyber.fairbid.ads.Interstitial
import com.fyber.fairbid.ads.interstitial.InterstitialListener
import com.google.errorprone.annotations.Keep
import com.monetizationlib.data.Monetization
import com.monetizationlib.data.ads.LoaderType
import com.monetizationlib.data.ads.LoaderTypeHelper
import com.monetizationlib.data.ads.MaxRevenueTracker
import com.monetizationlib.data.FastCashOutAdFormat
import com.monetizationlib.data.ads.model.GivvyAd
import com.monetizationlib.data.ads.model.GivvyAdType

@Keep
class FairBidInterstitialAdsLoader : InterstitialListener {

    private var requestAdPlacementId: String = ""
    private var netPayout = 0.0
    private var isInitialized = false
    private var impressionData: ImpressionData? = null

    companion object {
        private var instance: FairBidInterstitialAdsLoader? = null
        fun getInstance(): FairBidInterstitialAdsLoader? {
            if (instance == null) {
                synchronized(FairBidInterstitialAdsLoader::class.java) {
                    if (instance == null) {
                        instance = FairBidInterstitialAdsLoader()
                    }
                }
            }
            return instance
        }

        fun onDestroy() {
            instance = null
        }

        override fun toString(): String {
            return "FairBidInterstitialAdsLoader"
        }
    }

    fun initialize(placementId: String) {
        if (isInitialized) return
        Monetization.logWtfIfNeeded("FairBidInterstitialAdsLoader initialize")
        this.requestAdPlacementId = placementId
        Interstitial.setInterstitialListener(this)
        Interstitial.request(this.requestAdPlacementId)
        isInitialized = true
    }

    fun isAdAvailable(): Boolean {
        return Interstitial.isAvailable(requestAdPlacementId)
    }


    fun getInterstitialImpressionData(): ImpressionData? {
        return impressionData
    }

    fun getNetPayout(): Double? {
        return netPayout
    }

    fun showAd(): GivvyAd {
        LoaderTypeHelper.latestLoaderUsedType = LoaderType.FAIRBID
        if (Interstitial.isAvailable(requestAdPlacementId)) {
            Monetization.logWtfIfNeeded("FairBidInterstitialAdsLoader showAd called with isAvailable")
            (Monetization.getActivityOrContext() as? Activity)?.let {
                Monetization.logWtfIfNeeded("FairBidInterstitialAdsLoader activity = $it")
                Interstitial.show(requestAdPlacementId, it)
                return GivvyAd(providerName = getNetworkName(), true, GivvyAdType.Interstitial)
            }
            return GivvyAd(providerName = getNetworkName(), false, GivvyAdType.Interstitial)
        } else {
            Monetization.logWtfIfNeeded("FairBidInterstitialAdsLoader showAd called without isAvailable")
            Interstitial.request(requestAdPlacementId)
            return GivvyAd(providerName = getNetworkName(), false, GivvyAdType.Interstitial)
        }
    }

    override fun onShow(p0: String, p1: ImpressionData) {
        val netPayout: Double = p1.netPayout
        val currency: String = p1.currency
        val priceAccuracy: ImpressionData.PriceAccuracy = p1.priceAccuracy
        val impressionDepth: Int = p1.impressionDepth
        val message = java.lang.String.format(
            "FairBidInterstitialAdsLoader - Placement %s has been shown with a net payout of %f %s with accuracy: %s and impression depth: %s and %s",
            p0,
            netPayout,
            currency,
            priceAccuracy,
            impressionDepth,
            p1.jsonString
        )

        Monetization.logWtfIfNeeded(message)

        Monetization.onAdShown()

        Monetization.getActivityOrContext()?.let {
            MaxRevenueTracker.addImpressionData(
                it, p1,
                isInterstitial = true,
                isVideo = false,
                isBanner = false
            )
        }

        this.netPayout = 0.0
        this.impressionData = null
    }

    override fun onClick(placementId: String) {
        Monetization.logWtfIfNeeded("FairBidInterstitialAdsLoader onClick")
    }

    override fun onHide(placementId: String) {
        Monetization.logWtfIfNeeded("FairBidInterstitialAdsLoader onHide")
        Monetization.onInterstitialAdHidden()
    }

    override fun onShowFailure(placementId: String, impressionData: ImpressionData) {
        Monetization.logWtfIfNeeded("FairBidInterstitialAdsLoader onShowFailure with impressionDate $impressionData")
        Monetization.onAdFailedToShow("FairBidInterstitialAdsLoader onShowFailure", -11111)

        netPayout = 0.0
        this.impressionData = null

        isInitialized = false
        initialize(requestAdPlacementId)

        if (Monetization.monetizationConfig?.shouldForceSentImpressions == true) {
            Monetization.showBestAd()
        } else {
            Monetization.showInterstitial()
        }
    }

    override fun onAvailable(placementId: String) {
        if (!Interstitial.isAvailable(requestAdPlacementId)) {
            isInitialized = false
            initialize(requestAdPlacementId)
        } else {
            impressionData = Interstitial.getImpressionData(requestAdPlacementId)
            netPayout = getInterstitialImpressionData()?.netPayout ?: 0.0
            MaxRevenueTracker.onFairBidAdFetched(
                getInterstitialImpressionData(),
                FastCashOutAdFormat.INTERSTITIAL
            )
            Monetization.logWtfIfNeeded(
                "FairBidInterstitialAdsLoader onAvailable netPayout = ${getInterstitialImpressionData()?.netPayout}, \n impressionData = $impressionData"
            )
        }
    }

    override fun onUnavailable(placementId: String) {
        Monetization.logWtfIfNeeded("FairBidInterstitialAdsLoader onUnavailable")
    }

    override fun onRequestStart(placementId: String, requestId: String) {
        Monetization.logWtfIfNeeded("FairBidInterstitialAdsLoader onRequestStart with placementId = $placementId , requestId = $requestId")
    }

    private fun getNetworkName(): String? {
        return getInterstitialImpressionData()?.demandSource

    }

    fun isFacebookAd(): Boolean {
        return getNetworkName()?.contains("meta", true) == true || getNetworkName()?.contains(
            "facebook", true
        ) == true
    }

    override fun toString(): String {
        return "FairBidInterstitialAdsLoader"
    }
}
