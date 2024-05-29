package com.monetizationlib.data.attributes.model

import com.google.gson.annotations.SerializedName

data class DownloadFeatureActionCompleteRequest(
    @SerializedName("packageName") val packageName: String,
    @SerializedName("userId") val userId: String,
    @SerializedName("type") val stepType: String? = null,
    @SerializedName("downloadedApp") val downloadedAppName: String? = null,
    @SerializedName("timeInAppInSeconds") val secondsSpent: Int? = null,
)