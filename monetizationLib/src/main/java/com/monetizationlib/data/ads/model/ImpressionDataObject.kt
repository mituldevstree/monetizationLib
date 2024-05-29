package com.monetizationlib.data.ads.model

import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class ImpressionDataObject(
    @SerializedName("publisherRevenue") val publisherRevenue: Double?,
    @SerializedName("country") val country: String?,
    @SerializedName("adUnitFormat") val adUnitFormat: String?,
    @SerializedName("networkName") val networkName: String?,
    @SerializedName("hasBeenClicked") var hasBeenClicked: Boolean?,
    @SerializedName("network") var network: String?,
    @SerializedName("priceAccuracy") var priceAccuracy: String?,
    @SerializedName("jsonString") var jsonString: String?,
)
