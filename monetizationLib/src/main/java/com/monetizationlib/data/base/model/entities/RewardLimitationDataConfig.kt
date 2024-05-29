package com.monetizationlib.data.base.model.entities

import com.google.gson.annotations.SerializedName

class RewardLimitationDataConfig(
    @SerializedName("title") val title: String? = "",
    @SerializedName("description") val description: String? = "",
    @SerializedName("checkOutAppDesc") val checkOutAppDesc: String? = "",
    @SerializedName("redirectionAppList") val redirectionAppList: MutableList<RewardLimitationAppDetails>? = null,
)