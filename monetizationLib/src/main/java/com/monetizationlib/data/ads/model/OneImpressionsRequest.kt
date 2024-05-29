package com.monetizationlib.data.ads.model

import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class OneImpressionsRequest(
    @SerializedName("currentImpression")
    val currentImpression: ImpressionDataObject,
    @SerializedName("userId")
    val userId: String
)
