package com.monetizationlib.data.network.model

import androidx.annotation.Keep
import androidx.databinding.BaseObservable
import com.google.gson.annotations.SerializedName

@Keep
data class SessionInfo(
    @SerializedName("sessionId") var sessionId: String? = null,
    @SerializedName("user") var user: User? = null,
    @SerializedName("config") var userConfig: UserConfig? = null,
//    @SerializedName("subscription") var activeMemberShip: MemberShip? = null
) : BaseObservable()