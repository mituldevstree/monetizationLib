package com.monetizationlib.data.attributes.model

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.monetizationlib.data.Monetization
import com.monetizationlib.data.ads.model.ImpressionResponse
import com.monetizationlib.data.base.model.Model
import com.monetizationlib.data.base.model.networkLayer.layerSpecifics.Completable
import com.monetizationlib.data.base.viewModel.ResultAsyncState

object AttributesModel : Model() {
    var gson = Gson()

    override val networkFacade: AttributesNetworkFacade
        get() = AttributesNetworkFacade

    override val localStorage: AttributesStorage
        get() = AttributesStorage()

    fun saveConfig(monetizationConfig: MonetizationConfig, context: Context) {
        var configString = gson.toJson(monetizationConfig)
        Monetization.logWtfIfNeeded("monetizationConfigSaved, $configString")
        context.getSharedPreferences("basePrefFile", Context.MODE_PRIVATE)?.edit()
            ?.putString("monetizationConfig", configString)?.apply()
    }

    fun getOldConfig(context: Context): MonetizationConfig {
        return gson.fromJson(
            context.getSharedPreferences("basePrefFile", Context.MODE_PRIVATE)?.getString(
                "monetizationConfig", "{}"
            ), MonetizationConfig::class.java
        )
    }

    fun getConfig(
        appName: String,
        userId: String,
        context: Context,
        temporaryLiveData: MutableLiveData<ResultAsyncState<MonetizationConfig>>,
    ) {
        networkFacade.getConfig(success = {
            temporaryLiveData.postValue(ResultAsyncState.Success(it))
        }, failure = {
            temporaryLiveData.postValue(ResultAsyncState.Failed(it))
        }, appName = appName, userId = userId, context = context)
    }

    fun getDownloadFeatureConfig(
        requestParam: MutableMap<String, Any>,
        temporaryLiveData: MutableLiveData<ResultAsyncState<DownloadStepConfigResponse>>,
    ) {
        networkFacade.getDownloadFeatureConfig(success = {
            temporaryLiveData.postValue(ResultAsyncState.Success(it))
        }, failure = {
            temporaryLiveData.postValue(ResultAsyncState.Failed(it))
        },requestParam = requestParam)
    }

    fun sendDownloadFeatureStepComplete(
        requestParam: MutableMap<String, Any>,
        temporaryLiveData: MutableLiveData<ResultAsyncState<DownloadStepConfigResponse>>,
    ) {
        networkFacade.sendStepActionComplete(success = {
            temporaryLiveData.postValue(ResultAsyncState.Success(it))
        }, failure = {
            temporaryLiveData.postValue(ResultAsyncState.Failed(it))
        }, requestParam = requestParam)
    }

    fun getIpAddress(
        temporaryLiveData: MutableLiveData<ResultAsyncState<IpResponse>>,
    ) {
        networkFacade.getIpAddress(success = {
            temporaryLiveData.postValue(ResultAsyncState.Success(it))
        }, failure = {
            temporaryLiveData.postValue(ResultAsyncState.Failed(it))
        })
    }

    fun sendForegroundStatus(
        userId: String,
        advertId: String,
        isRooted: Boolean,
        temporaryLiveData: MutableLiveData<ResultAsyncState<ImpressionResponse?>>,
    ) {
        networkFacade.sendForegroundStatus(
            success = {
                Monetization.updateRecaptchaStatus(it?.needToShowSwipeCheck)
                temporaryLiveData.postValue(ResultAsyncState.Success(it))
            },
            failure = {
                temporaryLiveData.postValue(ResultAsyncState.Failed(it))
            },
            userId,
            isRooted = isRooted,
            advertId = advertId
        )
    }

    fun markUserAsReal(
        packageName: String,
        userId: String,
        temporaryLiveData: MutableLiveData<ResultAsyncState<Completable>>,
    ) {
        networkFacade.markUserAsReal(success = {
            temporaryLiveData.postValue(ResultAsyncState.Success(it))
        }, failure = {
            temporaryLiveData.postValue(ResultAsyncState.Failed(it))
        }, packageName = packageName, userId = userId)
    }

    fun giveRewardForDownloadedApp(
        packageName: String,
        isForNewAppDownLoad: Boolean = false,
        userId: String,
        temporaryLiveData: MutableLiveData<ResultAsyncState<RewardForNextAdResponse>>,
    ) {
        networkFacade.giveRewardForDownloadedApp(success = {
            temporaryLiveData.postValue(ResultAsyncState.Success(it))
        }, failure = {
            temporaryLiveData.postValue(ResultAsyncState.Failed(it))
        }, packageName = packageName, userId = userId, isForNewAppDownLoad = isForNewAppDownLoad)
    }
}