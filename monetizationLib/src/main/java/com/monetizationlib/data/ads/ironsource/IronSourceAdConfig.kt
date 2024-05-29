package com.monetizationlib.data.ads.ironsource

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class IronSourceAdConfig(
    @SerializedName("appId") val appId: String? = "1cb31df75",
    @SerializedName("interstitialAdUnit") val interstitialAdUnit: String? = "",
    @SerializedName("rewardedAdUnit") val rewardedAdUnit: String? = "",
    @SerializedName("bannerAdUnit") val bannerAdUnit: String? = "",
)