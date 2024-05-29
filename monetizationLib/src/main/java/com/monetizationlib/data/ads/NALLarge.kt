package com.monetizationlib.data.ads

import com.applovin.mediation.nativeAds.MaxNativeAdView


open class NALLarge : MNAL() {

    var impressionCountVar = 0

    companion object {
        private var NALDefaultAdsLoader: NALLarge? = null

        fun getDefaultLoader(): NALLarge? {
            synchronized(NALLarge::class.java) {
                if (NALDefaultAdsLoader == null) {
                    NALDefaultAdsLoader =
                        NALLarge()
                }
            }

            return NALDefaultAdsLoader
        }

        fun onDestroy(){
            NALDefaultAdsLoader?.onDestroy()
            NALDefaultAdsLoader = null
        }
    }

    override fun onImpressionMade() {
        impressionCountVar++
    }
    override fun getImpressionCount(): Int {
        return impressionCountVar
    }
    override fun onAdImpression(loadedAd: MaxNativeAdView?) {

    }

    override fun getDesignType(): AdDesignType? {
        return AdDesignType.LARGE
    }

    override fun getMaxAdLoadInBackground(): Int {
        return 0
    }

    override fun clear() {
        impressionCountVar = 0
    }

    override fun initialLoadAmount(): Int {
        return 2
    }

    override fun secondaryLoadAmount(): Int {
        return 2
    }

}
