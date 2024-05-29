package com.monetizationlib.data.attributes.model

import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class StartOfferResponse(@SerializedName("link") val link: String?)