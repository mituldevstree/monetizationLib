package com.monetizationlib.data.ads.model

import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class SendErrorMessageRequest(
    @SerializedName("userId") val userId: String,
    @SerializedName("packageName") val packageName: String,
    @SerializedName("message") val message: String,
)
