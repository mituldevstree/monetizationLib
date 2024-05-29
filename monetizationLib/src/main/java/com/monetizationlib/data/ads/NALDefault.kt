package com.monetizationlib.data.ads

import com.applovin.mediation.nativeAds.MaxNativeAdView


open class NALDefault : MNAL() {

    var impressionCountVar = 0

    companion object {
        private var NALDefaultAdsLoader: NALDefault? = null

        fun getDefaultLoader(): NALDefault? {
            synchronized(NALDefault::class.java) {
                if (NALDefaultAdsLoader == null) {
                    NALDefaultAdsLoader =
                        NALDefault()
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

    override fun onAdImpression(loadedAd: MaxNativeAdView?) {

    }

    override fun getDesignType(): AdDesignType? {
        return AdDesignType.MEDIUM
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
