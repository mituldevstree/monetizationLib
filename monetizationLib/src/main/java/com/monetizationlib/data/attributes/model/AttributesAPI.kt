package com.monetizationlib.data.attributes.model

import com.monetizationlib.data.ads.model.*
import com.monetizationlib.data.base.model.entities.StatusRequest
import com.monetizationlib.data.base.model.networkLayer.layerSpecifics.ApiResponse
import com.monetizationlib.data.base.model.networkLayer.layerSpecifics.Completable
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AttributesAPI {
    @POST("/getConfig")
    fun getConfig(@Body configRequest:ConfigRequest): Call<ApiResponse<MonetizationConfig>>

    @POST("/saveAdError")
    fun saveAdError(@Body configRequest:AdErrorRequest): Call<ApiResponse<Completable>>

    @POST("/getStatus")
    fun saveImpressionData(@Body request: ImpressionsRequest): Call<ApiResponse<ImpressionResponse>>

    @POST("/sendErrorMessage")
    fun sendErrorMessage(@Body request: SendErrorMessageRequest): Call<ApiResponse<Completable>>

    @POST("/getChatStatus")
    fun saveChatImpressionData(@Body request: ImpressionsRequest): Call<ApiResponse<Completable>>
    @POST("/sendForegroundStatus")
    fun sendForegroundStatus(@Body feedRequest: StatusRequest): Call<ApiResponse<ImpressionResponse>>

    @POST("/markUserAsReal")
    fun markUserAsReal(@Body configRequest:MarkUserAsRealRequest): Call<ApiResponse<Completable>>

    @POST("/checkIfEligibleForAdditionalReward")
    fun checkIfEligibleForAdditionalReward(@Body request: EligibleForRewardRequest): Call<ApiResponse<EligibleForRewardResponse>>

    @POST("/getFastReward")
    fun getFastReward(@Body request: GetFastRewardRequest): Call<ApiResponse<GetFastRewardResponse>>

    @POST("/saveFastWithdrawData")
    fun saveFastWithdrawData(@Body request: SaveFastRewardDataRequest): Call<ApiResponse<Completable>>

    @POST("/giveRewardForDownloadedApp")
    fun giveRewardForDownloadedApp(@Body testRequest: NextAdRewardRequest): Call<ApiResponse<RewardForNextAdResponse>>

    @POST("/newAppIsDownloaded")
    fun giveRewardForNewAppIsDownloaded(@Body testRequest: NextAdRewardRequest): Call<ApiResponse<RewardForNextAdResponse>>

    @POST("/getDownloadFeatureConfig")
    fun getDownloadFeatureConfig(@Body params: MutableMap<String, Any>): Call<ApiResponse<DownloadStepConfigResponse>>

    @POST("/completeStepDownloadFeatureConfig")
    fun communicateActionComplete(@Body params: MutableMap<String, Any>): Call<ApiResponse<DownloadStepConfigResponse>>

}