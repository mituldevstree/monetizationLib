package com.monetizationlib.data.attributes.businessModule

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.monetizationlib.data.ads.model.ImpressionResponse
import com.monetizationlib.data.attributes.model.AttributesModel
import com.monetizationlib.data.attributes.model.DownloadFeatureActionCompleteRequest
import com.monetizationlib.data.attributes.model.DownloadStepConfigResponse
import com.monetizationlib.data.attributes.model.StepsResponse
import com.monetizationlib.data.attributes.model.IpResponse
import com.monetizationlib.data.attributes.model.MonetizationConfig
import com.monetizationlib.data.attributes.model.RewardForNextAdResponse
import com.monetizationlib.data.base.businessModule.BusinessModule
import com.monetizationlib.data.base.model.networkLayer.layerSpecifics.Completable
import com.monetizationlib.data.base.viewModel.ResultAsyncState
import kotlinx.coroutines.flow.Flow

object AttributesBussinesModule : BusinessModule<AttributesModel>() {
    override val model: AttributesModel
        get() = AttributesModel

    fun geConfig(
        appName: String,
        userId: String,
        context: Context,
    ): MutableLiveData<ResultAsyncState<MonetizationConfig>> {
        val temporaryLiveData = MutableLiveData<ResultAsyncState<MonetizationConfig>>()

        temporaryLiveData.postValue(ResultAsyncState.Started())

        model.getConfig(appName, userId, context, temporaryLiveData)

        return temporaryLiveData
    }

    fun getDownloadFeatureConfig(
        requestParam: MutableMap<String, Any>
    ): MutableLiveData<ResultAsyncState<DownloadStepConfigResponse>> {
        val temporaryLiveData = MutableLiveData<ResultAsyncState<DownloadStepConfigResponse>>()

        temporaryLiveData.postValue(ResultAsyncState.Started())

        model.getDownloadFeatureConfig(requestParam, temporaryLiveData)

        return temporaryLiveData
    }

    fun sendDownloadFeatureStepComplete(
        requestParam: MutableMap<String, Any>,
    ): LiveData<ResultAsyncState<DownloadStepConfigResponse>> {
        val temporaryLiveData = MutableLiveData<ResultAsyncState<DownloadStepConfigResponse>>()
        temporaryLiveData.postValue(ResultAsyncState.Started())
        model.sendDownloadFeatureStepComplete(
            requestParam = requestParam,
            temporaryLiveData = temporaryLiveData
        )
        return temporaryLiveData
    }

    fun getIpAddress(
    ): MutableLiveData<ResultAsyncState<IpResponse>> {
        val temporaryLiveData = MutableLiveData<ResultAsyncState<IpResponse>>()

        temporaryLiveData.postValue(ResultAsyncState.Started())

        model.getIpAddress(temporaryLiveData)

        return temporaryLiveData
    }

    /*fun startOffer(
        userId: String,
        packageName: String,
        appName: String
    ): MutableLiveData<ResultAsyncState<StartOfferResponse>> {
        val temporaryLiveData = MutableLiveData<ResultAsyncState<StartOfferResponse>>()

        temporaryLiveData.postValue(ResultAsyncState.Started())

        model.startOffer(
            temporaryLiveData,
            userId = userId,
            packageName = packageName,
            appName = appName
        )

        return temporaryLiveData
    }

    fun getOfferwallForAndroidUser(
        userId: String,
        packageName: String,
    ): MutableLiveData<ResultAsyncState<OffersSectionOffer?>> {
        val temporaryLiveData = MutableLiveData<ResultAsyncState<OffersSectionOffer?>>()

        temporaryLiveData.postValue(ResultAsyncState.Started())

        model.getOfferwallForAndroidUser(
            temporaryLiveData, userId = userId,
            packageName = packageName,
        )

        return temporaryLiveData
    }

    fun getOfferwallForUser(
        userId: String,
        packageName: String,
    ): MutableLiveData<ResultAsyncState<OfferwallResponse>> {
        val temporaryLiveData = MutableLiveData<ResultAsyncState<OfferwallResponse>>()

        temporaryLiveData.postValue(ResultAsyncState.Started())

        model.getOfferwallForUser(appName = packageName, userId, temporaryLiveData)

        return temporaryLiveData
    }

    fun getOfferwallForUserNewStyle(
        userId: String,
        packageName: String,
    ): MutableLiveData<ResultAsyncState<MutableList<OffersSectionOffer>>> {
        val temporaryLiveData = MutableLiveData<ResultAsyncState<MutableList<OffersSectionOffer>>>()

        temporaryLiveData.postValue(ResultAsyncState.Started())

        model.getOfferwallForUserNewStyle(appName = packageName, userId, temporaryLiveData)

        return temporaryLiveData
    }

    fun completeSiteRegistration(
        userId: String,
        packageName: String,
    ): MutableLiveData<ResultAsyncState<EarnedCredits>> {
        val temporaryLiveData = MutableLiveData<ResultAsyncState<EarnedCredits>>()

        temporaryLiveData.postValue(ResultAsyncState.Started())

        model.completeSiteRegistration(appName = packageName, userId = userId, temporaryLiveData)

        return temporaryLiveData
    }

    fun startOffer(
        appName: String,
        userId: String
    ): MutableLiveData<ResultAsyncState<StartOfferResponse>> {
        val temporaryLiveData = MutableLiveData<ResultAsyncState<StartOfferResponse>>()

        temporaryLiveData.postValue(ResultAsyncState.Started())

        model.startOffer(temporaryLiveData, appName, userId)

        return temporaryLiveData
    }

    fun getIntroOffers(
        appName: String,
        userId: String
    ): MutableLiveData<ResultAsyncState<GetIntroOffers>> {
        val temporaryLiveData = MutableLiveData<ResultAsyncState<GetIntroOffers>>()

        temporaryLiveData.postValue(ResultAsyncState.Started())

        model.getIntroOffer(temporaryLiveData, appName, userId)

        return temporaryLiveData
    }

    fun updateOfferForApp(
        appName: String,
        userId: String
    ): MutableLiveData<ResultAsyncState<OffersSectionOffer?>> {
        val temporaryLiveData = MutableLiveData<ResultAsyncState<OffersSectionOffer?>>()

        temporaryLiveData.postValue(ResultAsyncState.Started())

        model.updateStatusForApp(temporaryLiveData, appName, userId)

        return temporaryLiveData
    }*/

    fun sendForegroundStatus(
        userId: String,
        advertId: String,
        isRooted: Boolean,
    ): MutableLiveData<ResultAsyncState<ImpressionResponse?>> {
        val temporaryLiveData = MutableLiveData<ResultAsyncState<ImpressionResponse?>>()

        temporaryLiveData.postValue(ResultAsyncState.Started())

        model.sendForegroundStatus(
            userId = userId,
            advertId = advertId,
            isRooted = isRooted,
            temporaryLiveData
        )

        return temporaryLiveData
    }

    fun markUserAsReal(
        packageName: String,
        userId: String,
    ): MutableLiveData<ResultAsyncState<Completable>> {
        val temporaryLiveData = MutableLiveData<ResultAsyncState<Completable>>()

        temporaryLiveData.postValue(ResultAsyncState.Started())

        model.markUserAsReal(packageName, userId, temporaryLiveData)

        return temporaryLiveData
    }

    fun giveRewardForDownloadedApp(
        packageName: String,
        isForNewAppDownLoad: Boolean = false,
        userId: String,
    ): MutableLiveData<ResultAsyncState<RewardForNextAdResponse>> {
        val temporaryLiveData = MutableLiveData<ResultAsyncState<RewardForNextAdResponse>>()

        temporaryLiveData.postValue(ResultAsyncState.Started())

        model.giveRewardForDownloadedApp(
            packageName,
            isForNewAppDownLoad,
            userId,
            temporaryLiveData
        )

        return temporaryLiveData
    }
}