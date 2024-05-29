package com.monetizationlib.data.ads.model

import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class ImpressionsRequest(
    @SerializedName("userId")
    val userId: String,
    @SerializedName("date")
    val date: String,
    @SerializedName("advertisementId")
    val advertisementId: String,
    @SerializedName("usedVpnInSession")
    val usedVpnInSession: Boolean,
    @SerializedName("currentImpressionsList")
    val currentImpressionsList: List<ImpressionDataObject>,
    @SerializedName("clickJson")
    var clickJson:String
)
