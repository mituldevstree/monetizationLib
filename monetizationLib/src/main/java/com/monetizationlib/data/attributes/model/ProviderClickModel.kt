package com.monetizationlib.data.attributes.model

import com.google.gson.annotations.SerializedName

class ProviderClickModel {
    @SerializedName("providerClickModelName")
    var providerClickModelName: String? = ""

    @SerializedName("clickPercentageTopRange")
    var clickPercentageTopRange: Double? = 75.0

    @SerializedName("clickPercentageBottomRange")
    var clickPercentageBottomRange: Double? = 25.0

    @SerializedName("clickPercentageDepth")
    var clickPercentageDepth: Int? = 0
}