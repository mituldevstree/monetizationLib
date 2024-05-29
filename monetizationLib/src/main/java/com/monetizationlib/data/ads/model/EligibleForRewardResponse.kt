package com.monetizationlib.data.ads.model

import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class EligibleForRewardResponse(
    @SerializedName("expectedRewardInCredits")
    val expectedRewardInCredits: Int,
    @SerializedName("expectedReward")
    val expectedReward: String,
    @SerializedName("canCashoutWithPayeer")
    val canCashoutWithPayeer: Boolean,
    @SerializedName("canCashout")
    val canCashout: Boolean,
    @SerializedName("minimalCpmForSpecialReward")
    val minimalCpmForSpecialReward: Double,
    @SerializedName("fastRewardConfig") var fastRewardConfig: FastRewardConfig?
)