package com.monetizationlib.data.ads.fairbid

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.ViewGroup
import com.fyber.FairBid
import com.fyber.fairbid.ads.FairBidListener
import com.fyber.fairbid.ads.mediation.MediatedNetwork
import com.fyber.fairbid.user.UserInfo
import com.fyber.inneractive.sdk.external.InneractiveAdManager
import com.monetizationlib.data.Monetization
import com.monetizationlib.data.ads.AppLovinAdsInitializerManager
import com.monetizationlib.data.ads.ConsentHandler
import com.monetizationlib.data.ads.TimerUtils
import com.monetizationlib.data.attributes.model.AttributesNetworkFacade


class FairBidInitializerManager private constructor(private var context: Context?) {

    var bannerViewGroup: ViewGroup? = null
    var hasShownConsentThisSession = false

    fun initialize(
        activity: Activity,
        userId: String,
        fairBidAdConfig: FairBidAdConfig?,
        bannerViewGroup: ViewGroup?,
        showConsent: (needToShowConsent:Boolean) -> Unit,
    ) {
        this.bannerViewGroup = bannerViewGroup
        Monetization.logWtfIfNeeded("FairBidInitializerManager initialize method is called successfully")

        InneractiveAdManager.setLogLevel(Log.ERROR);

        FairBid.Settings.setMuted(Monetization.shouldMuteAds)

        val fairBidErrorMessage: StringBuilder = StringBuilder()
        FairBid.configureForAppId(fairBidAdConfig?.appId ?: "128409")
            .withFairBidListener(object : FairBidListener {
                override fun mediationFailedToStart(errorMessage: String, errorCode: Int) {
                    if (errorMessage.contains("Network not configured", ignoreCase = true)) return
                    Monetization.logWtfIfNeeded("FairBid mediationFailedToStart errorCode = $errorCode & errorMessage = $errorMessage")
                    fairBidErrorMessage.append("mediationFailedToStart errorCode = $errorCode and errorMessage = $errorMessage \n ")
                }

                override fun mediationStarted() {}

                override fun onNetworkFailedToStart(
                    network: MediatedNetwork,
                    errorMessage: String
                ) {
                    if (errorMessage.contains("Network not configured", ignoreCase = true)) return
                    Monetization.logWtfIfNeeded(
                        "FairBid onNetworkFailedToStart isStarted = " +
                                "${FairBid.assertStarted()} & networkName = ${network.name} & networkVersion = ${network.version} & errorMessage = $errorMessage"
                    )
                    fairBidErrorMessage.append("onNetworkFailedToStart & networkName = ${network.name} & networkVersion = ${network.version} & errorMessage = $errorMessage \n ")
                }

                override fun onNetworkStarted(network: MediatedNetwork) {}

            }).start(activity)

        UserInfo.setUserId(userId)

        sendErrorMessage(userId, fairBidErrorMessage.toString())

        ConsentHandler.handleConsent(activity, initAds = {
            initAds(activity)
        })

        val shouldAskForConsent = ConsentHandler.shouldAskForConsentGeneral(activity)

        if (shouldAskForConsent) {
            showConsent.invoke(true)
        } else {
            initAds(activity)
            showConsent.invoke(false)
        }
    }

    private fun sendErrorMessage(userId: String, fairBidErrorMessage: String) {
        try {
            TimerUtils.postDelayedInBackgroundThread({
                if (!FairBid.assertStarted() || fairBidErrorMessage.isEmpty()) return@postDelayedInBackgroundThread
                Monetization.logWtfIfNeeded("fairBidErrorMessage = $fairBidErrorMessage")
                AttributesNetworkFacade.sendErrorMessage(
                    userId,
                    packageName = Monetization.packageName,
                    fairBidErrorMessage,
                    {},
                    {})
            }, 30_000)
        } catch (exc: Exception) {
            Monetization.logWtfIfNeeded("exception fairBidErrorMessage = ${exc.message}")
        }
    }

    fun showAndHandleConsent(
        baseActivity: Activity, success: () -> Unit?, onAdsInit: () -> Unit? = {},
    ): Boolean {
        ConsentHandler.handleConsent(activity = baseActivity) {
            initAds(baseActivity)
            onAdsInit.invoke()
            success()
        }

        val shouldAskForConsent = ConsentHandler.shouldAskForConsentGeneral(baseActivity)

        if (shouldAskForConsent) {
            if (hasShownConsentThisSession) {
                initAds(baseActivity)
                onAdsInit.invoke()
                success()
                return true
            }

            hasShownConsentThisSession = true

            ConsentHandler.showConsent(baseActivity) {
                initAds(baseActivity, true)
                onAdsInit.invoke()
                success()
            }

            return true
        } else {
            initAds(baseActivity, true)
            onAdsInit.invoke()
            success()
            return false
        }
    }

    private fun initAds(baseActivity: Activity, isAfterConsent: Boolean = false) {
        FairBidInterstitialAdsLoader.getInstance()?.initialize(
            Monetization.monetizationConfig?.fairBidAdConfig?.interstitialAdUnit ?: "1154819"
        )
        FairBidVideoAdsLoader.getInstance()?.initialize(
            Monetization.monetizationConfig?.fairBidAdConfig?.rewardedAdUnit ?: "1157997"
        )
        FairBidBannerAdsLoader.getInstance()?.initialize(
            Monetization.monetizationConfig?.fairBidAdConfig?.bannerAdUnit ?: "1158004",
            Monetization.bannerViewGroup
        )

        if ((Monetization.monetizationConfig?.shouldUseApplovin == true || Monetization.monetizationConfig?.shouldUseApplovinBackfill == true) && (hasShownConsentThisSession || isAfterConsent)) {
            AppLovinAdsInitializerManager.getInstance(baseActivity)?.initAds(baseActivity)
        }

        if (Monetization.monetizationConfig?.shouldUseIronSourceMediation == true && (hasShownConsentThisSession || isAfterConsent)) {
            AppLovinAdsInitializerManager.getInstance(baseActivity)?.initAds(baseActivity)
        }
    }

    companion object {
        private var instance: FairBidInitializerManager? = null
        fun getInstance(context: Context): FairBidInitializerManager? {
            if (instance == null) {
                synchronized(FairBidInitializerManager::class.java) {
                    if (instance == null) {
                        instance = FairBidInitializerManager(context)
                    }
                }
            }
            return instance
        }

        internal fun onDestroy() {
            instance?.bannerViewGroup = null
            instance?.context = null
            instance = null
        }
    }
}
