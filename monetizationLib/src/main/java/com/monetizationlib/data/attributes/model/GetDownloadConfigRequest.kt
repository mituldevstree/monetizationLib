package com.monetizationlib.data.attributes.model

import com.google.gson.annotations.SerializedName

data class GetDownloadConfigRequest(
    @SerializedName("packageName") val packageName: String,
    @SerializedName("userId") val userId: String
)