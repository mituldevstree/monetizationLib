package com.monetizationlib.data.ads.model

import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class SurveyRewardConfig(
    @SerializedName("surveyTitle") val surveyTitle: String?,
    @SerializedName("surveyDesc") val surveyDesc: String?,
    @SerializedName("surveyTimeDesc") val surveyTimeDesc: String?,
    @SerializedName("surveyCoinsDesc") val surveyCoinsDesc: String?,
    @SerializedName("offerwallTitle") val offerwallTitle: String?,
    @SerializedName("offerwallDesc") val offerwallDesc: String?,
    @SerializedName("offerwalTimeDesc") val offerwalTimeDesc: String?,
    @SerializedName("offerwalCoinsDesc") val offerwalCoinsDesc: String?
)