package com.monetizationlib.data.ads.model

import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class SaveFastRewardDataRequest(
    @SerializedName("provider")
    val provider: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("userId")
    val userId: String
)
