package com.monetizationlib.data.ads.model

import com.google.gson.annotations.SerializedName
import com.monetizationlib.data.base.model.entities.RewardLimitationDataConfig
import androidx.annotation.Keep

@Keep
data class ImpressionResponse(
    @SerializedName("shouldShowInfoDialog")
    val shouldShowInfoDialog: Boolean?,
    @SerializedName("shouldForceClose")
    val shouldForceClose: Boolean?,
    @SerializedName("dialogText")
    val dialogText: String?,
    @SerializedName("appLink")
    val appLink: String?,
    @SerializedName("needToShowSwipeCheck")
    val needToShowSwipeCheck: Boolean?,
    @SerializedName("shouldShowNextAdOffer") var shouldShowNextAdOffer: Boolean = true,
    @SerializedName("shouldForceFinishTheApp") var shouldForceFinishTheApp: Boolean? = false,
    @SerializedName("rewardLimitationDataConfig") val rewardLimitationDataConfig: RewardLimitationDataConfig?,
)