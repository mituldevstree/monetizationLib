package com.monetizationlib.data.attributes.viewModel

import android.content.Context
import com.monetizationlib.data.attributes.businessModule.AttributesBussinesModule
import com.monetizationlib.data.base.viewModel.BaseViewModel

class AttributesViewModel : BaseViewModel<AttributesBussinesModule>() {
    override val businessModule: AttributesBussinesModule
        get() = AttributesBussinesModule

    fun getConfig(appName: String, userId: String, context: Context) =
        businessModule.geConfig(appName, userId, context)

    fun sendForegroundStatus(
        userId: String, advertId: String, isRooted: Boolean
    ) = businessModule.sendForegroundStatus(userId, advertId, isRooted)

    fun markUserAsReal(packageName: String, userId: String) =
        businessModule.markUserAsReal(packageName, userId)

    fun giveRewardForDownloadedApp(
        packageName: String,
        userId: String,
        isForNewAppDownLoad: Boolean = false,
    ) =
        businessModule.giveRewardForDownloadedApp(
            packageName,
            isForNewAppDownLoad = isForNewAppDownLoad,
            userId
        )
}