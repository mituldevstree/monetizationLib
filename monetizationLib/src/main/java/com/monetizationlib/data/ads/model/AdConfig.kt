package com.monetizationlib.data.ads.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

//{'interstitialAdUnit': '2d74c7c0653c07b1', 'rewardedAdUnit': '2741b73fc9b9bb77',
// 'bannerAdUnit': '3b83fb4b9d9b3efa',
// 'nativeMediumAdUnit': '097255195c1964b1',
// 'nativeLargeAdUnit': '
// ', 'nativeSmallAdUnit': '', 'nativeAdditionalOneAdUnit': '', 'nativeAdditionalTwoAdUnit': ''}
@Keep
data class AdConfig(
    @SerializedName("waterfallInterstitialAdUnit") val waterfallInterstitialAdUnit: String? = "",
    @SerializedName("interstitialAdUnit") val interstitialAdUnit: String? = "",
    @SerializedName("interstitialTopBidAdUnit") val interstitialTopBidAdUnit: String? = "",
    @SerializedName("interstitialMidBidAdUnit") val interstitialMidBidAdUnit: String? = "",
    @SerializedName("shouldUseAppLovinTopBid") val shouldUseAppLovinTopBid: Boolean? = false,
    @SerializedName("shouldUseAppLovinMidBid") val shouldUseAppLovinMidBid: Boolean? = false,
    @SerializedName("waterfallRewardedAdUnit") val waterfallRewardedAdUnit: String? = "",
    @SerializedName("rewardedAdUnit") val rewardedAdUnit: String? = "",
    @SerializedName("bannerAdUnit") val bannerAdUnit: String? = "",
    @SerializedName("nativeMediumAdUnit") val nativeMediumAdUnit: String? = "",
    @SerializedName("nativeLargeAdUnit") var nativeLargeAdUnit: String? = "",
    @SerializedName("nativeSmallAdUnit") var nativeSmallAdUnit: String? = "",
    @SerializedName("nativeAdditionalOneAdUnit") var nativeAdditionalOneAdUnit: String? = "",
    @SerializedName("nativeAdditionalTwoAdUnit") val nativeAdditionalTwoAdUnit: String? = "",
) {
    fun getListOfAllAdUnits(): MutableList<String> {
        val mutableListOfAdUnits = mutableListOf<String>()
        if (!waterfallInterstitialAdUnit.isNullOrEmpty()) mutableListOfAdUnits.add(
            waterfallInterstitialAdUnit
        )
        if (!interstitialAdUnit.isNullOrEmpty()) mutableListOfAdUnits.add(interstitialAdUnit)
        if (!interstitialTopBidAdUnit.isNullOrEmpty()) mutableListOfAdUnits.add(
            interstitialTopBidAdUnit
        )
        if (!interstitialMidBidAdUnit.isNullOrEmpty()) mutableListOfAdUnits.add(
            interstitialMidBidAdUnit
        )
        if (!waterfallRewardedAdUnit.isNullOrEmpty()) mutableListOfAdUnits.add(
            waterfallRewardedAdUnit
        )
        if (!rewardedAdUnit.isNullOrEmpty()) mutableListOfAdUnits.add(rewardedAdUnit)
        if (!bannerAdUnit.isNullOrEmpty()) mutableListOfAdUnits.add(bannerAdUnit)
        if (!nativeMediumAdUnit.isNullOrEmpty()) mutableListOfAdUnits.add(nativeMediumAdUnit)
        if (!nativeLargeAdUnit.isNullOrEmpty()) mutableListOfAdUnits.add(nativeLargeAdUnit!!)
        if (!nativeSmallAdUnit.isNullOrEmpty()) mutableListOfAdUnits.add(nativeSmallAdUnit!!)
        if (!nativeAdditionalOneAdUnit.isNullOrEmpty()) mutableListOfAdUnits.add(
            nativeAdditionalOneAdUnit!!
        )
        if (!nativeAdditionalTwoAdUnit.isNullOrEmpty()) mutableListOfAdUnits.add(
            nativeAdditionalTwoAdUnit
        )

        return mutableListOfAdUnits
    }
}
