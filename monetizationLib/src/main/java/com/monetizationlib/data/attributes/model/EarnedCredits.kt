package com.monetizationlib.data.attributes.model

import com.google.gson.annotations.SerializedName

open class EarnedCredits(
    @SerializedName("earnCredits") val earnedCredits: Int,
    @SerializedName("userCredits") var userCoinsCount: Int = 0,
    @SerializedName("percentOfMinCashOut") var percentOfMinCashOut: Double? = null,
    @SerializedName("userBalanceDouble") var currentUserBalance: Double = 0.0,
)