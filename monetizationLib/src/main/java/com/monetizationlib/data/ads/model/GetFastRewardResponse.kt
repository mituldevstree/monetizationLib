package com.monetizationlib.data.ads.model

import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class GetFastRewardResponse(
    @SerializedName("credits") val credits: Int,
    @SerializedName("earnCredits") val earnedCredits: Int,
    @SerializedName("userBalance") var userBalance: String = "",
)