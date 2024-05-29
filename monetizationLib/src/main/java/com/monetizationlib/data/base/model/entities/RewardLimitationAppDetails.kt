package com.monetizationlib.data.base.model.entities

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class RewardLimitationAppDetails(
    @SerializedName("appName") val appName: String? = "",
    @SerializedName("appLogo") val appLogo: String? = "",
    @SerializedName("appPackageName") val appPackageName: String? = "",
    @SerializedName("isMiningApp") val isMiningApp: Boolean? = false,
    @SerializedName("message") val message: String? = "",
    @SerializedName("buttonMessage") val buttonMessage: String? = "",
    @SerializedName("link") val link: String? = "",
)