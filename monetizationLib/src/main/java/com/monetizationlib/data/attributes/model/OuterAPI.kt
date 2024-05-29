package com.monetizationlib.data.attributes.model

import retrofit2.Call
import retrofit2.http.POST

interface OuterAPI {
    @POST("/api/json")
    fun getIp(): Call<IpResponse>
}