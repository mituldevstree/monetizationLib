package com.monetizationlib.data.ads.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class FastRewardConfig(
    @SerializedName("fastCashOutTitleOne") val fastCashOutTitleOne: String? = "",
    @SerializedName("fastCashOutDescOne") val fastCashOutDescOne: String? = "",
    @SerializedName("fastCashOutTitleTwo") val fastCashOutTitleTwo: String? = "",
    @SerializedName("fastCashOutDescTwo") val fastCashOutDescTwo: String? = "",
    @SerializedName("fastCashOutTitleLast") val fastCashOutTitleLast: String? = "",
    @SerializedName("fastCashOutDescLast") val fastCashOutDescLast: String? = "",
    @SerializedName("numberOfMaxAds") val numberOfMaxAds: Int? = 3,
    @SerializedName("bigRewardTitleOne") val bigRewardTitleOne: String? = "",
    @SerializedName("bigRewardDescOne") val bigRewardDescOne: String? = "",
)