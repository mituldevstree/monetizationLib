package com.monetizationlib.data.attributes.model

import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class RewardForNextAdResponse(
    @SerializedName("shouldShowNextAdOffer") val shouldShowNextAdOffer: Boolean?,
    @SerializedName("credits") val credits: Int?,
    @SerializedName("earnCredits") val earnCredits: Int?,
    @SerializedName("userBalance") var userBalance: String? = null,
    @SerializedName("userBalanceWithCurrency") var userBalanceWithCurrency: String? = null,
    @SerializedName("downloadProgressViewAppsCount") var downloadProgressViewAppsCount: String? = null,
    @SerializedName("downloadProgressViewCurrentAppsCount") val downloadProgressViewCurrentAppsCount: String? = null,
    @SerializedName("percentOfMinCashOut") var percentOfMinCashOut: Double? = null,
    @SerializedName("downloadProgressViewDesc") var downloadProgressViewDesc: String? = "Download 5 apps which show when you are watching an AD",
    @SerializedName("downloadProgressViewButtonText") var downloadProgressViewButtonText: String? = "Got it",
    @SerializedName("downloadProgressViewTitle") var downloadProgressViewTitle: String = "How to win"
)