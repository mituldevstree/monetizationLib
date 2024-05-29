package com.monetizationlib.data.network.model

import androidx.annotation.Keep
import androidx.databinding.BaseObservable
import com.google.gson.annotations.SerializedName

@Keep
data class AppConfig(
    @SerializedName("version") var versionName: String? = null,
    @SerializedName("hasUpdate") var hasUpdate: Boolean = false,
    @SerializedName("forceUpdate") var isForceUpdate: Boolean = false,
    @SerializedName("isForGiwy") var isForGiwy: Boolean = false,
    @SerializedName("date") var date: Long? = 0L
) : BaseObservable()