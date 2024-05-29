package com.monetizationlib.data.network.domain

import androidx.annotation.Keep
import com.monetizationlib.data.network.ApiEndpoints
import com.monetizationlib.data.network.data.ObjectBaseModel
import com.monetizationlib.data.network.model.AppConfig
import com.monetizationlib.data.network.model.SessionInfo
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Response

@Keep
interface GeneralApiService {

    @POST(ApiEndpoints.VERSION_CHECK)
    suspend fun versionCheck(@Body params: MutableMap<String, Any>): Response<ObjectBaseModel<AppConfig>>

    @POST(ApiEndpoints.REGISTER_DEVICE)
    suspend fun registerDevice(@Body params: MutableMap<String, Any>): Response<ObjectBaseModel<SessionInfo>>

}