package com.monetizationlib.data.ads.model

import com.google.gson.annotations.SerializedName

class AdErrorRequest(
    @SerializedName("userId")
    val userId: String,
    @SerializedName("adError")
    val adError: String,
)
