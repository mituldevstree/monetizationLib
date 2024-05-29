package com.monetizationlib.data.attributes.model

import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class IpResponse(
    @SerializedName("ipAddress") val ip: String? = null,
    @SerializedName("ipVersion") val ipVersion: Int? = -1,
    @SerializedName("latitude") val latitude: Double? = 0.0,
    @SerializedName("longitude") val longitude: Double? = 0.0,
    @SerializedName("countryName") val countryName: String? = null,
    @SerializedName("countryCode") val countryCode: String? = null,
    @SerializedName("timeZone") val timeZone: String? = null,
    @SerializedName("zipCode") val zipCode: String? = null,
    @SerializedName("cityName") val cityName: String? = null,
    @SerializedName("regionName") val regionName: String? = null,
)