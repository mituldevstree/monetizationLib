package com.monetizationlib.data.attributes.model

import android.content.Context
import android.content.Context.TELEPHONY_SERVICE
import android.os.Build
import android.telephony.TelephonyManager
import android.util.Log
import com.monetizationlib.data.Monetization
import com.monetizationlib.data.ads.model.AdErrorRequest
import com.monetizationlib.data.ads.model.EligibleForRewardRequest
import com.monetizationlib.data.ads.model.EligibleForRewardResponse
import com.monetizationlib.data.ads.model.GetFastRewardRequest
import com.monetizationlib.data.ads.model.GetFastRewardResponse
import com.monetizationlib.data.ads.model.ImpressionDataObject
import com.monetizationlib.data.ads.model.ImpressionResponse
import com.monetizationlib.data.ads.model.ImpressionsRequest
import com.monetizationlib.data.ads.model.SaveFastRewardDataRequest
import com.monetizationlib.data.ads.model.SendErrorMessageRequest
import com.monetizationlib.data.base.model.LocaleManager
import com.monetizationlib.data.base.model.entities.StatusRequest
import com.monetizationlib.data.base.model.networkLayer.layerSpecifics.ApiError
import com.monetizationlib.data.base.model.networkLayer.layerSpecifics.Completable
import com.monetizationlib.data.base.model.networkLayer.layerSpecifics.NetworkFacade
import com.monetizationlib.data.base.model.networkLayer.networking.DefaultCallback
import com.monetizationlib.data.base.model.networkLayer.networking.NetworkManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Body
import java.io.IOException
import java.util.Calendar
import java.util.TimeZone

object AttributesNetworkFacade : NetworkFacade<AttributesAPI>() {
    override val apiService: AttributesAPI
        get() = NetworkManager.retrofit.create(AttributesAPI::class.java)

    val apiServiceOuter: OuterAPI?
        get() {
            if (NetworkManager.retrofitOuter == null) {
                try {
                    NetworkManager.reinitRetrofitOfferwall()
                } catch (_: Exception) {
                    Monetization.logWtfIfNeeded("retrofitOfferwall is null")
                }
            }
            return NetworkManager.retrofitOuter?.create(OuterAPI::class.java)
        }

//    val apiServiceOfferwall: OfferwallAPI?
//        get() {
//            if (NetworkManager.retrofitOfferwall == null) {
//                try {
//                    NetworkManager.reinitRetrofitOfferwall()
//                } catch (_: Exception) {
//                    Monetization.logWtfIfNeeded("retrofitOfferwall is null")
//                }
//            }
//            return NetworkManager.retrofitOfferwall?.create(OfferwallAPI::class.java)
//        }

    fun getConfig(
        appName: String,
        userId: String,
        success: (MonetizationConfig) -> Unit,
        failure: (ApiError) -> Unit,
        context: Context,
    ) {
        try {


            //        Log.e("ShortsCatch","MODEL: " + Build.MODEL);
//        Log.e("ShortsCatch","ID: " + Build.ID);
//        Log.e("ShortsCatch","Manufacture: " + Build.MANUFACTURER);
//        Log.e("ShortsCatch","brand: " + Build.BRAND);
//        Log.e("ShortsCatch","type: " + Build.TYPE);
//        Log.e("ShortsCatch","user: " + Build.USER);
//        Log.e("ShortsCatch","BASE: " + Build.VERSION_CODES.BASE);
//        Log.e("ShortsCatch","INCREMENTAL " + Build.VERSION.INCREMENTAL);
//        Log.e("ShortsCatch","HARDWARE " + Build.HARDWARE);
//        Log.e("ShortsCatch","SDK  " + Build.VERSION.SDK);
//        Log.e("ShortsCatch","BOARD: " + Build.BOARD);
//        Log.e("ShortsCatch","BRAND " + Build.BRAND);
//        Log.e("ShortsCatch","BOOTLOADER " + Build.BOOTLOADER);
//        Log.e("ShortsCatch","HOST " + Build.HOST);
//        Log.e("ShortsCatch","FINGERPRINT: "+Build.TIME);
//        Log.e("ShortsCatch","TIME: "+Build.FINGERPRINT);
//        Log.e("ShortsCatch","Version Code: " + Build.VERSION.RELEASE);
//        Log.e("ShortsCatch","SIM Operator Name: "+ (this.getSystemService(TELEPHONY_SERVICE) as TelephonyManager).simOperatorName);
//        Log.e("ShortsCatch","Network Country Iso: "+ (this.getSystemService(TELEPHONY_SERVICE) as TelephonyManager).networkCountryIso);
//        Log.e("ShortsCatch","Network operator name: "+ (this.getSystemService(TELEPHONY_SERVICE) as TelephonyManager).networkOperatorName);

            apiService.getConfig(
                ConfigRequest(
                    appName,
                    userId,
                    true,
                    Build.MODEL,
                    Build.ID,
                    Build.MANUFACTURER,
                    Build.BRAND,
                    Build.TYPE,
                    Build.USER,
                    Build.HARDWARE,
                    Build.BOARD,
                    Build.BOOTLOADER,
                    Build.HOST,
                    Build.FINGERPRINT,
                    Build.VERSION_CODES.BASE.toString(),
                    (context.getSystemService(TELEPHONY_SERVICE) as TelephonyManager).simOperatorName,
                    (context.getSystemService(TELEPHONY_SERVICE) as TelephonyManager).networkCountryIso,
                    (context.getSystemService(TELEPHONY_SERVICE) as TelephonyManager).networkOperatorName,

                    )
            ).enqueue(object : DefaultCallback<MonetizationConfig>() {
                override fun onSuccess(response: MonetizationConfig) {
                    success.invoke(response)
                }

                override fun onFail(apiError: ApiError) = failure.invoke(apiError)
            })
        } catch (exception: IOException) {
            failure.invoke(ApiError())
        }
    }

    fun getDownloadFeatureConfig(
        requestParam: MutableMap<String, Any>,
        success: (DownloadStepConfigResponse) -> Unit,
        failure: (ApiError) -> Unit,
    ) {
        try {
            apiService.getDownloadFeatureConfig(
                requestParam
            ).enqueue(object : DefaultCallback<DownloadStepConfigResponse>() {
                override fun onSuccess(response: DownloadStepConfigResponse) {
                    success.invoke(response)
                }

                override fun onFail(apiError: ApiError) = failure.invoke(apiError)
            })
        } catch (exception: IOException) {
            failure.invoke(ApiError())
        }
    }

    fun sendStepActionComplete(
        requestParam: MutableMap<String, Any>,
        success: (DownloadStepConfigResponse) -> Unit,
        failure: (ApiError) -> Unit,
    ) {
        try {
            apiService.communicateActionComplete(
                requestParam
            ).enqueue(object : DefaultCallback<DownloadStepConfigResponse>() {
                override fun onSuccess(response: DownloadStepConfigResponse) {
                    success.invoke(response)
                }

                override fun onFail(apiError: ApiError) = failure.invoke(apiError)
            })
        } catch (exception: IOException) {
            failure.invoke(ApiError())
        }
    }


    fun sendImpressionData(
        currentImpressions: List<ImpressionDataObject>,
        userId: String,
        success: (ImpressionResponse) -> Unit,
        failure: (ApiError) -> Unit,
    ) {
        try {
            val calendar: Calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"))
            val timeInMili: Long = calendar.timeInMillis

            Log.wtf("FAIRBID", "Sending imp data $currentImpressions")
            val context = Monetization.getActivityOrContext()
            var clickJson = ""
            if (context != null) {
                clickJson = LocaleManager.sharedPreferencesToJson(context)
            }

            apiService.saveImpressionData(
                ImpressionsRequest(
                    userId = userId,
                    date = timeInMili.toString(),
                    Monetization.getAdIdQuick(),
                    Monetization.isUsignVpn == true,
                    currentImpressions,
                    clickJson
                )
            ).enqueue(object : DefaultCallback<ImpressionResponse>() {
                override fun onSuccess(response: ImpressionResponse) {
                    Monetization.updateRecaptchaStatus(response.needToShowSwipeCheck)
                    Monetization.updateShowNextAdOffer(response.shouldShowNextAdOffer)

                    if (response.rewardLimitationDataConfig != null) {
                        Monetization.monetizationConfig?.rewardLimitationDataConfig =
                            response.rewardLimitationDataConfig
                    }

                    if (Monetization.monetizationConfig?.rewardLimitationDataConfig == null && response.shouldForceFinishTheApp == true) {
                        Monetization.onShouldForceFinishTheApp()
                    }
                    success.invoke(response)
                }

                override fun onFail(apiError: ApiError) {
                    failure.invoke(apiError)
                }
            })
        } catch (exception: IOException) {
        }
    }

    fun sendChatImpressionData(
        currentImpressions: List<ImpressionDataObject>,
        userId: String,
        success: (Completable) -> Unit,
        failure: (ApiError) -> Unit,
    ) {
        try {
            val calendar: Calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"))
            val timeInMili: Long = calendar.timeInMillis

            val context = Monetization.getActivityOrContext()
            var clickJson = ""
            if (context != null) {
                clickJson = LocaleManager.sharedPreferencesToJson(context)
            }

            apiService.saveChatImpressionData(
                ImpressionsRequest(
                    userId = userId,
                    date = timeInMili.toString(),
                    Monetization.getAdIdQuick(),
                    Monetization.isUsignVpn == true,
                    currentImpressions,
                    clickJson
                )
            ).enqueue(object : DefaultCallback<Completable>() {
                override fun onSuccess(response: Completable) {
                    success.invoke(response)
                }

                override fun onFail(apiError: ApiError) {
                    failure.invoke(apiError)
                }
            })
        } catch (exception: IOException) {
        }
    }

    fun getIpAddress(
        success: (IpResponse) -> Unit,
        failure: (ApiError) -> Unit,
    ) {
        try {
            apiServiceOuter?.getIp()?.enqueue(object : Callback<IpResponse> {
                override fun onResponse(call: Call<IpResponse>, response: Response<IpResponse>) {
                    success.invoke(response.body() ?: IpResponse(""))
                }

                override fun onFailure(call: Call<IpResponse>, t: Throwable) {
                    failure.invoke(ApiError(300))
                }

            })
        } catch (exception: IOException) {
            failure.invoke(ApiError())
        }
    }

    fun sendErrorMessage(
        userId: String,
        packageName: String,
        message: String,
        success: (Completable) -> Unit,
        failure: (ApiError) -> Unit,
    ) {
        try {
            apiService.sendErrorMessage(SendErrorMessageRequest(userId = userId, packageName = packageName, message = message))
                .enqueue(object : DefaultCallback<Completable>() {
                    override fun onSuccess(response: Completable) {
                        success.invoke(response)
                    }

                    override fun onFail(apiError: ApiError) = failure.invoke(apiError)
                })
        } catch (exception: IOException) {
            failure.invoke(ApiError())
        }
    }

    /* fun startOffer(
         success: (StartOfferResponse) -> Unit,
         failure: (ApiError) -> Unit,
         userId: String,
         packageName: String,
         appName: String,
     ) {
         try {
             apiServiceOfferwall?.startOffer(
                 OfferwallRequest(
                     packageName = packageName,
                     userId = userId,
                     appName = appName
                 )
             )
                 ?.enqueue(object : DefaultCallback<StartOfferResponse>() {
                     override fun onSuccess(response: StartOfferResponse) {
                         success.invoke(response)
                     }

                     override fun onFail(apiError: ApiError) = failure.invoke(apiError)
                 })
         } catch (exception: IOException) {
             failure.invoke(ApiError())
         }
     }

     fun getOfferwallForAndroidUser(
         success: (OffersSectionOffer?) -> Unit,
         failure: (ApiError) -> Unit,
         userId: String,
         packageName: String,
     ) {
         try {
             apiServiceOfferwall?.getOfferwallForAndroidUser(
                 OfferwallRequest(
                     packageName = packageName,
                     userId = userId,
                     appName = ""
                 )
             )
                 ?.enqueue(object : DefaultCallback<OffersSectionOffer?>() {
                     override fun onSuccess(response: OffersSectionOffer?) {
                         success.invoke(response)
                     }

                     override fun onFail(apiError: ApiError) = failure.invoke(apiError)
                 })
         } catch (exception: IOException) {
             failure.invoke(ApiError())
         }
     }*/

    fun sendForegroundStatus(
        success: (ImpressionResponse?) -> Unit,
        failure: (ApiError) -> Unit,
        userId: String,
        isRooted: Boolean,
        advertId: String,
    ) {
        try {
            apiService.sendForegroundStatus(
                StatusRequest(
                    userId = userId,
                    packageName = Monetization.packageName,
                    advertisementId = advertId,
                    isRooted = isRooted
                )
            ).enqueue(object : DefaultCallback<ImpressionResponse>() {
                override fun onSuccess(response: ImpressionResponse) {
                    if (response.rewardLimitationDataConfig != null) {
                        Monetization.monetizationConfig?.rewardLimitationDataConfig =
                            response.rewardLimitationDataConfig
                    }
                    success.invoke(response)
                }

                override fun onFail(apiError: ApiError) = failure.invoke(apiError)
            })
        } catch (exception: IOException) {
            failure.invoke(ApiError())
        }
    }

    /*  fun getOfferwallForUser(
          success: (OfferwallResponse) -> Unit,
          failure: (ApiError) -> Unit,
          userId: String,
          packageName: String,
      ) {
          try {
              apiServiceOfferwall?.getOfferwallForUser(
                  OfferwallRequest(
                      packageName = packageName,
                      userId
                  )
              )
                  ?.enqueue(object : DefaultCallback<OfferwallResponse>() {
                      override fun onSuccess(response: OfferwallResponse) {
                          success.invoke(response)
                      }

                      override fun onFail(apiError: ApiError) = failure.invoke(apiError)
                  })
          } catch (exception: IOException) {
              failure.invoke(ApiError())
          }
      }

      fun getOfferwallForUserNewStyle(
          success: (MutableList<OffersSectionOffer>) -> Unit,
          failure: (ApiError) -> Unit,
          userId: String,
          packageName: String,
      ) {
          try {
              apiServiceOfferwall?.getOfferwallForUserNewStyle(
                  OfferwallRequest(
                      packageName = packageName,
                      userId
                  )
              )
                  ?.enqueue(object : DefaultCallback<MutableList<OffersSectionOffer>>() {
                      override fun onSuccess(response: MutableList<OffersSectionOffer>) {
                          success.invoke(response)
                      }

                      override fun onFail(apiError: ApiError) = failure.invoke(apiError)
                  })
          } catch (exception: IOException) {
              failure.invoke(ApiError())
          }
      }

      fun completeSiteRegistration(
          success: (EarnedCredits) -> Unit,
          failure: (ApiError) -> Unit,
          userId: String,
          packageName: String,
      ) {
          try {
              apiServiceOfferwall?.completeSiteRegistration(
                  OfferwallRequest(
                      packageName = packageName,
                      userId
                  )
              )
                  ?.enqueue(object : DefaultCallback<EarnedCredits>() {
                      override fun onSuccess(response: EarnedCredits) {
                          success.invoke(response)
                      }

                      override fun onFail(apiError: ApiError) = failure.invoke(apiError)
                  })
          } catch (exception: IOException) {
              failure.invoke(ApiError())
          }
      }

      fun getIntroOffers(
          success: (GetIntroOffers) -> Unit,
          failure: (ApiError) -> Unit,
          userId: String,
          packageName: String,
      ) {
          try {
              apiServiceOfferwall?.getIntroOffer(
                  OfferwallRequest(
                      packageName = packageName,
                      userId = userId,
                  )
              )
                  ?.enqueue(object : DefaultCallback<GetIntroOffers>() {
                      override fun onSuccess(response: GetIntroOffers) {
                          success.invoke(response)
                      }

                      override fun onFail(apiError: ApiError) = failure.invoke(apiError)
                  })
          } catch (exception: IOException) {
              failure.invoke(ApiError())
          }
      }

      fun updateStatusForApp(
          success: (OffersSectionOffer) -> Unit,
          failure: (ApiError) -> Unit,
          userId: String,
          packageName: String,
          appName: String,
      ) {
          try {
              apiServiceOfferwall?.updateStatusForApp(
                  OfferwallRequest(
                      packageName = packageName,
                      userId = userId,
                      appName = appName,
                  )
              )
                  ?.enqueue(object : DefaultCallback<OffersSectionOffer>() {
                      override fun onSuccess(response: OffersSectionOffer) {
                          success.invoke(response)
                      }

                      override fun onFail(apiError: ApiError) = failure.invoke(apiError)
                  })
          } catch (exception: IOException) {
              failure.invoke(ApiError())
          }
      }*/

    fun markUserAsReal(
        packageName: String,
        userId: String,
        success: (Completable) -> Unit,
        failure: (ApiError) -> Unit,
    ) {
        try {
            apiService.markUserAsReal(MarkUserAsRealRequest(packageName, userId))
                .enqueue(object : DefaultCallback<Completable>() {
                    override fun onSuccess(response: Completable) {
                        success.invoke(response)
                    }

                    override fun onFail(apiError: ApiError) = failure.invoke(apiError)
                })
        } catch (exception: IOException) {
            failure.invoke(ApiError())
        }
    }

    fun giveRewardForDownloadedApp(
        packageName: String,
        userId: String,
        isForNewAppDownLoad: Boolean = false,
        success: (RewardForNextAdResponse) -> Unit,
        failure: (ApiError) -> Unit,
    ) {
        try {
            val callBack = object : DefaultCallback<RewardForNextAdResponse>() {
                override fun onSuccess(response: RewardForNextAdResponse) {
                    success.invoke(response)
                }

                override fun onFail(apiError: ApiError) {
                    failure.invoke(apiError)
                }
            }
            if (isForNewAppDownLoad) {
                apiService.giveRewardForNewAppIsDownloaded(NextAdRewardRequest(packageName, userId))
                    .enqueue(callBack)
            } else {
                apiService.giveRewardForDownloadedApp(NextAdRewardRequest(packageName, userId))
                    .enqueue(callBack)
            }
        } catch (exception: IOException) {
            failure.invoke(ApiError())
        }
    }

    fun checkIfEligibleForAdditionalReward(
        cpm: Double,
        userId: String,
        success: (EligibleForRewardResponse) -> Unit,
        failure: (ApiError) -> Unit,
    ) {
        try {
            apiService.checkIfEligibleForAdditionalReward(EligibleForRewardRequest(cpm, userId))
                .enqueue(object : DefaultCallback<EligibleForRewardResponse>() {
                    override fun onSuccess(response: EligibleForRewardResponse) {
                        Monetization.monetizationConfig?.minimalCpmForSpecialReward =
                            response.minimalCpmForSpecialReward
                        Monetization.monetizationConfig?.fastRewardConfig =
                            response.fastRewardConfig
                        success.invoke(response)
                    }

                    override fun onFail(apiError: ApiError) = failure.invoke(apiError)
                })
        } catch (exception: IOException) {
            failure.invoke(ApiError())
        }
    }

    fun getFastReward(
        isWithWithdraw: Boolean,
        userId: String,
        success: (GetFastRewardResponse) -> Unit,
        failure: (ApiError) -> Unit,
    ) {
        try {
            apiService.getFastReward(GetFastRewardRequest(isWithWithdraw = isWithWithdraw, userId))
                .enqueue(object : DefaultCallback<GetFastRewardResponse>() {
                    override fun onSuccess(response: GetFastRewardResponse) {
                        success.invoke(response)
                    }

                    override fun onFail(apiError: ApiError) = failure.invoke(apiError)
                })
        } catch (exception: IOException) {
            failure.invoke(ApiError())
        }
    }

    fun saveFastWithdrawData(
        provider: String,
        email: String,
        userId: String,
        success: (Completable) -> Unit,
        failure: (ApiError) -> Unit,
    ) {
        try {
            apiService.saveFastWithdrawData(
                SaveFastRewardDataRequest(
                    provider = provider, email = email, userId
                )
            ).enqueue(object : DefaultCallback<Completable>() {
                override fun onSuccess(response: Completable) {
                    success.invoke(response)
                }

                override fun onFail(apiError: ApiError) = failure.invoke(apiError)
            })
        } catch (exception: IOException) {
            failure.invoke(ApiError())
        }
    }

    fun saveFastWithdrawData(
        userId: String,
        adError: String,
        success: (Completable) -> Unit,
        failure: (ApiError) -> Unit,
    ) {
        try {
            apiService.saveAdError(
                AdErrorRequest(
                    userId = userId, adError = adError
                )
            ).enqueue(object : DefaultCallback<Completable>() {
                override fun onSuccess(response: Completable) {
                    success.invoke(response)
                }

                override fun onFail(apiError: ApiError) = failure.invoke(apiError)
            })
        } catch (exception: IOException) {
            failure.invoke(ApiError())
        }
    }
}
