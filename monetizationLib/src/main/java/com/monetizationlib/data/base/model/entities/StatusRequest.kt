package com.monetizationlib.data.base.model.entities

import com.google.gson.annotations.SerializedName
import com.monetizationlib.data.attributes.model.IpResponse
import androidx.annotation.Keep

@Keep
data class StatusRequest(
    @SerializedName("userId") val userId: String,
    @SerializedName("isRooted") val isRooted: Boolean = false,
    @SerializedName("packageName") val packageName: String,
    @SerializedName("advertisementId") val advertisementId: String,
)