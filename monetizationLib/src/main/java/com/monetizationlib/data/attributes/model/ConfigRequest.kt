package com.monetizationlib.data.attributes.model

import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class ConfigRequest(
    @SerializedName("appName") val appName: String,
    @SerializedName("userId") val userId: String,
    @SerializedName("isNewStyle") val isNewStyle: Boolean,
    @SerializedName("buildModel") var buildModel: String? = "",
    @SerializedName("buildId") var buildId: String? = "",
    @SerializedName("buildManufacture") var buildManufacture: String? = "",
    @SerializedName("buildBrand") var buildBrand: String? = "",
    @SerializedName("buildType") var buildType: String? = "",
    @SerializedName("buildUser") var buildUser: String? = "",
    @SerializedName("buildHardware") var buildHardware: String? = "",
    @SerializedName("buildBoard") var buildBoard: String? = "",
    @SerializedName("buildBootloader") var buildBootloader: String? = "",
    @SerializedName("buildHost") var buildHost: String? = "",
    @SerializedName("buildFingerprint") var buildFingerprint: String? = "",
    @SerializedName("buildVersionCode") var buildVersionCode: String? = "",
    @SerializedName("buildSON") var buildSimOperatorName: String? = "",
    @SerializedName("buildNCI") var buildNCI: String? = "",
    @SerializedName("buildNON") var buildNON: String? = "",
)