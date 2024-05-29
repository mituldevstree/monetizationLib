//package com.monetizationlib.data.service
//
//import android.content.BroadcastReceiver
//import android.content.Context
//import android.content.Intent
//import android.content.Intent.ACTION_PACKAGE_ADDED
//import android.util.Log
//import com.google.gson.Gson
//import com.monetizationlib.data.AdRewardType
//import com.monetizationlib.data.Monetization
//import com.monetizationlib.data.attributes.model.NextAdRewardRequest
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlin.coroutines.CoroutineContext
//
//class PackageChangeReceiver : BroadcastReceiver(), CoroutineScope {
//    override fun onReceive(context: Context, intent: Intent) {
//        val action = intent.action
//        val data = intent.data
//        val packageAdv = data?.encodedSchemeSpecificPart
//        //TODO Add logic to open app here
//        Monetization.logWtfIfNeeded("Install Completed--$action--$packageAdv--adRewardType = ${Monetization.adRewardType}")
//        when (action) {
//            ACTION_PACKAGE_ADDED -> {
//                if(Monetization.adRewardType == AdRewardType.NEXT_AD_FEATURE) {
//                    Monetization.adRewardType = AdRewardType.NONE
//                    Monetization.showAndGetRewardForNextAd(data?.encodedSchemeSpecificPart)
//                }
//                if(Monetization.adRewardType == AdRewardType.DOWNLOAD_APPS_FEATURE) {
//                    Monetization.adRewardType = AdRewardType.NONE
//                    Monetization.callApiForNewAppDownload(successBlock = {
//                        Log.e("Api Success", Gson().toJson(it))
//                        if (it.downloadProgressViewAppsCount.isNullOrEmpty().not()) {
//                            Monetization.monetizationConfig?.setDownloadProgressViewAppsCount(
//                                it.downloadProgressViewAppsCount
//                            )
//                        }
//                        if (it.downloadProgressViewCurrentAppsCount.isNullOrEmpty().not()) {
//                            Monetization.monetizationConfig?.setDownloadProgressViewCurrentAppsCount(
//                                it.downloadProgressViewCurrentAppsCount
//                            )
//                        }
//
//                    }, failure = {
//                        Log.e("Api Error", Gson().toJson(it))
//                    })
//                }
//                context.unregisterReceiver(this)
//            }
//        }
//
//    }
//
//    override val coroutineContext: CoroutineContext
//        get() = Dispatchers.Main
//
//}