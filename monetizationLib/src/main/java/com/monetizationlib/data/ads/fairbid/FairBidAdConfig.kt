package com.monetizationlib.data.ads.fairbid

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class FairBidAdConfig(
    @SerializedName("appId") val appId: String? = "",
    @SerializedName("interstitialAdUnit") val interstitialAdUnit: String? = "",
    @SerializedName("rewardedAdUnit") val rewardedAdUnit: String? = "",
    @SerializedName("bannerAdUnit") val bannerAdUnit: String? = "",
)