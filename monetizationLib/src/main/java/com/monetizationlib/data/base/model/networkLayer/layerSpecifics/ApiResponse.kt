package com.monetizationlib.data.base.model.networkLayer.layerSpecifics

import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class ApiResponse<T>(
    @SerializedName("statusCode")
    val statusCode: Int,

    @SerializedName("statusText")
    val statusText: String,

    @SerializedName("statusDesc")
    val userError: String?,

    @SerializedName("result")
    val entity: T
)

fun ApiResponse<*>.toError() = ApiError(this.statusCode, this.statusText, this.userError)

