package com.monetizationlib.data.ads.fairbid

import android.app.Activity
import android.util.Log
import android.view.View.VISIBLE
import android.view.ViewGroup
import com.fyber.fairbid.ads.Banner
import com.fyber.fairbid.ads.ImpressionData
import com.fyber.fairbid.ads.banner.BannerError
import com.fyber.fairbid.ads.banner.BannerListener
import com.fyber.fairbid.ads.banner.BannerOptions
import com.monetizationlib.data.Monetization
import com.monetizationlib.data.ads.MaxRevenueTracker
import com.monetizationlib.data.ads.Utility


class FairBidBannerAdsLoader : BannerListener {
    private var placementId: String = ""

    companion object {
        private var instance: FairBidBannerAdsLoader? = null
        fun getInstance(): FairBidBannerAdsLoader? {
            if (instance == null) {
                synchronized(FairBidBannerAdsLoader::class.java) {
                    if (instance == null) {
                        instance = FairBidBannerAdsLoader()
                    }
                }
            }
            return instance
        }

        fun onDestroy() {
            instance?.placementId?.let { wrappedPlacementId ->
                Banner.destroy(wrappedPlacementId)
            }
            instance = null
        }
    }

    fun initialize(placementId: String, viewGroup: ViewGroup?) {
        Utility.executeOnUIThread  {
            this.placementId = placementId
            Banner.setBannerListener(this)
            viewGroup?.let {
                val bannerOptions: BannerOptions = BannerOptions().placeInContainer(viewGroup)
                (Monetization.getActivityOrContext() as? Activity)?.let {
                    Banner.show(this.placementId, bannerOptions, it)
                    viewGroup.visibility = VISIBLE
                }
            }
        }
    }

//     TODO - Comment this method and add new updated method
//    override fun onError(p0: String, p1: BannerError?) {
//        Monetization.logWtfIfNeeded("FairBidBannerAdsLoader onError")
//        Monetization.onFairBidBannerFailedToLoad()
//    }

    override fun onError(placementId: String, error: BannerError) {
        Monetization.logWtfIfNeeded("FairBidBannerAdsLoader onError")
        Monetization.onFairBidBannerFailedToLoad()
    }

    override fun onLoad(p0: String) {
        Monetization.onFairBidBannerLoad()
        Monetization.logWtfIfNeeded("FairBidBannerAdsLoader onLoad")
    }

    override fun onShow(p0: String, p1: ImpressionData) {
        Monetization.logWtfIfNeeded("FairBidBannerAdsLoader onShow")
        val netPayout: Double = p1.netPayout
        val currency: String = p1.currency
        val priceAccuracy: ImpressionData.PriceAccuracy = p1.priceAccuracy
        val impressionDepth: Int = p1.impressionDepth
        val message = java.lang.String.format(
            "Placement %s has been shown with a net payout of %f %s with accuracy: %s and impression depth: %s and %s",
            p0,
            netPayout,
            currency,
            priceAccuracy,
            impressionDepth,
            p1.jsonString
        )

        Log.wtf("FAIRBID", message)
        Monetization.getActivityOrContext()?.let {
            MaxRevenueTracker.addImpressionData(
                it, p1,
                isInterstitial = false,
                isVideo = false,
                isBanner = true
            )
        }
    }

    override fun onClick(p0: String) {
        Monetization.logWtfIfNeeded("FairBidBannerAdsLoader onClick")
    }

    override fun onRequestStart(placementId: String, requestId: String) {
        Monetization.logWtfIfNeeded("FairBidBannerAdsLoader onRequestStart with requestId = $requestId")
    }
}
