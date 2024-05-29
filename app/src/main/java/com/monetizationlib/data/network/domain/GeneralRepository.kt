package com.monetizationlib.data.network.domain



import com.monetizationlib.data.network.client.RetroClient.GENERAL_API_SERVICE
import com.monetizationlib.data.network.data.BaseApiResponse
import com.monetizationlib.data.network.data.ObjectBaseModel
import com.monetizationlib.data.network.model.AppConfig
import com.monetizationlib.data.network.model.SessionInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlin.coroutines.CoroutineContext

object GeneralRepository: BaseApiResponse(), CoroutineScope {

    suspend fun versionCheck(
        param: MutableMap<String, Any>
    ): Flow<ObjectBaseModel<AppConfig>?> {
        return flow<ObjectBaseModel<AppConfig>?> {
            kotlin.runCatching {
                emit(safeApiCall { GENERAL_API_SERVICE.versionCheck(param) })
            }
        }.flowOn(coroutineContext)
    }


    suspend fun registerDevice(
        param: MutableMap<String, Any>,
        dispatcher: CoroutineContext,
    ): Flow<ObjectBaseModel<SessionInfo>?> {
        return flow<ObjectBaseModel<SessionInfo>?> {
            kotlin.runCatching {
                emit(safeApiCall { GENERAL_API_SERVICE.registerDevice(param) })
            }
        }.flowOn(dispatcher)
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO


}