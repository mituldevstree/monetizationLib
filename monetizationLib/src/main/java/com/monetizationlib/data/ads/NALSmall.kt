package com.monetizationlib.data.ads

import com.applovin.mediation.nativeAds.MaxNativeAdView


open class NALSmall : MNAL() {

    var impressionCountVar = 0

    companion object {
        private var nativeAdsLoader: NALSmall? = null

        fun getDefaultLoader(): NALSmall? {
            synchronized(NALSmall::class.java) {
                if (nativeAdsLoader == null) {
                    nativeAdsLoader =
                        NALSmall()
                }
            }

            return nativeAdsLoader
        }

        fun onDestroy(){
            nativeAdsLoader?.onDestroy()
            nativeAdsLoader = null
        }
    }

    override fun onImpressionMade() {
        impressionCountVar++
    }

    override fun onAdImpression(loadedAd: MaxNativeAdView?) {

    }

    override fun getDesignType(): AdDesignType? {
        return AdDesignType.SMALL
    }

    override fun getMaxAdLoadInBackground(): Int {
        return 0
    }

    override fun initialLoadAmount(): Int {
        return 2
    }

    override fun secondaryLoadAmount(): Int {
        return  2
    }

    override fun getImpressionCount(): Int {
        return impressionCountVar
    }

    override fun clear() {
        impressionCountVar = 0
    }
}
