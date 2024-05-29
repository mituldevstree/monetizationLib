package com.monetizationlib.data.attributes.model

import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class MarkUserAsRealRequest(
    @SerializedName("packageName") val packageName: String,
    @SerializedName("userId") val userId: String
)