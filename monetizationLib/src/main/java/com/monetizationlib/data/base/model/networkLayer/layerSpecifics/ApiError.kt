package com.monetizationlib.data.base.model.networkLayer.layerSpecifics

import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class ApiError(
    @SerializedName("statusCode")
    val statusCode: Int = -1,
    @SerializedName("statusText")
    val statusText: String? = "Request Failed",
    @SerializedName("statusDesc")
    val userError: String? = "No Internet Connection"
)
