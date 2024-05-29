package com.monetizationlib.data.ads.model

import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class GetFastRewardRequest(
    @SerializedName("isWithWithdraw")
    val isWithWithdraw: Boolean,
    @SerializedName("userId")
    val userId: String
)
