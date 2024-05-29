package com.monetizationlib.data.ads

import com.monetizationlib.data.Monetization


object Ads {

    fun getMediumAdsLoader(): INativeAdsLoader? {
        if (Monetization.nativeXmlOne != null) {
            return NALDefault.getDefaultLoader()
        }

        return null
    }

    fun getLargeAdsLoader(): INativeAdsLoader? {
        if (Monetization.nativeXmlTwo != null) {
            return NALLarge.getDefaultLoader()
        }

        return null
    }

    fun getSmallAdsLoader(): INativeAdsLoader? {
        if (Monetization.nativeXmlThree != null) {
            return NALSmall.getDefaultLoader()
        }

        return null
    }

    fun getAdditionalAdsLoader(): INativeAdsLoader? {
        if (Monetization.nativeXmlFour != null) {
            return NALAdditional.getDefaultLoader()
        }

        return null
    }
}