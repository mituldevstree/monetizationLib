package com.monetizationlib.data.ads.model

import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class EligibleForRewardRequest(
    @SerializedName("cpm")
    val cpm: Double,
    @SerializedName("userId")
    val userId: String
)
