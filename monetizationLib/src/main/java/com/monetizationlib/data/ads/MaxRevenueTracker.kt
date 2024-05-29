package com.monetizationlib.data.ads

import android.content.Context
import android.os.Bundle
import android.telephony.TelephonyManager
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdFormat
import com.applovin.mediation.MaxAdRevenueListener
import com.applovin.sdk.AppLovinSdk
import com.fyber.fairbid.ads.ImpressionData
import com.google.firebase.analytics.FirebaseAnalytics
import com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo
import com.monetizationlib.data.FastCashOutAdFormat
import com.monetizationlib.data.Monetization
import com.monetizationlib.data.ads.model.GivvyAd
import com.monetizationlib.data.ads.model.ImpressionDataObject
import com.monetizationlib.data.ads.model.ImpressionResponse
import com.monetizationlib.data.attributes.model.AttributesNetworkFacade
import com.monetizationlib.data.base.view.FastCashOutProgressAlertDialog

object MaxRevenueTracker {
    var currentImpressions = mutableListOf<ImpressionDataObject>();
    var chatCurrentImpressions = mutableListOf<ImpressionDataObject>();

    var maxRevenuTracker: MaxAdRevenueListener? = null
    var isInChatFlow: Boolean = false

    var isApplovin = true

    var shouldMarkNextImpressionClicked = false

    var isInAd = false

    private var rewardedVideoMaxAd: Pair<MaxAd?, Boolean> = Pair(null, false)
    private var rewardedVideoMaxAdLessCPM: Pair<MaxAd?, Boolean> = Pair(null, false)

    fun getMaxRevenueTracker(context: Context): MaxAdRevenueListener? {
        if (maxRevenuTracker == null) {
            maxRevenuTracker = MaxAdRevenueListener { ad ->
                if (LoaderTypeHelper.latestLoaderUsedType != LoaderType.APPLOVIN) return@MaxAdRevenueListener

                Monetization.logWtfIfNeeded("getMaxRevenueTracker with Ad = $ad")
                val impressionDataObject = ImpressionDataObject(
                    ad.revenue,
                    AppLovinSdk.getInstance(context).configuration.countryCode,
                    ad.format?.displayName,
                    ad.networkName,
                    false,
                    "applovin",
                    priceAccuracy = "applovin_priceAccuracy",
                    jsonString = "applovin_jsonString"
                )

                //Logs Firebase event for Ad paid if needed
                logFirebaseEventForAdPaid(context, ad)

                if (isInChatFlow) {
                    chatCurrentImpressions.add(
                        impressionDataObject
                    )

                    if (ad.format?.displayName != "banner") {
                        sendChatImpressionData()
                    } else {
                        if (chatCurrentImpressions.size == 10) {
                            sendChatImpressionData()
                        }
                    }
                } else {
                    currentImpressions.add(impressionDataObject)
                    sendImpDataForced()
                }
            }
        }
        return maxRevenuTracker
    }

    fun addImpressionData(
        context: Context,
        impData: ImpressionData,
        isInterstitial: Boolean,
        isVideo: Boolean,
        isBanner: Boolean,
    ) {
        //Logs Firebase event for Ad paid if needed
        logFirebaseEventForAdPaid(context, impData)

        var format = ""
        if (isInterstitial) {
            format = "Interstitial"
        }

        if (isVideo) {
            format = "Rewarded"
        }

        if (isBanner) {
            format = "Banner"
        }

        if (isInChatFlow) {
            chatCurrentImpressions.add(
                ImpressionDataObject(
                    impData.netPayout,
                    impData.countryCode,
                    format,
                    impData.demandSource,
                    false,
                    "fyber",
                    impData.priceAccuracy.toString(),
                    impData.jsonString
                )
            )

            if (format != "BANNER") {
                sendChatImpressionData()
            } else {
                if (chatCurrentImpressions.size == 10) {
                    sendChatImpressionData()
                }
            }
        } else {
            currentImpressions.add(
                ImpressionDataObject(
                    impData.netPayout,
                    impData.countryCode,
                    format,
                    impData.demandSource,
                    false,
                    "fyber",
                    impData.priceAccuracy.toString(),
                    impData.jsonString
                )
            )
            sendImpDataForced()
        }
    }

    fun addImpressionIronSourceAdInfo(
        context: Context,
        adInfo: AdInfo
    ) {
        val impressionDataObject = ImpressionDataObject(
            adInfo.revenue,
            (context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).networkCountryIso,
            adInfo.adUnit,
            adInfo.adNetwork,
            false,
            "ironsource",
            priceAccuracy = adInfo.precision,
            jsonString = "ironsource_jsonString"
        )

        Monetization.logWtfIfNeeded("addImpressionIronSourceAdInfo impressionDataObject = $impressionDataObject")

        if (isInChatFlow) {
            chatCurrentImpressions.add(impressionDataObject)

            if (adInfo.adUnit != "Banner") {
                sendChatImpressionData()
            } else {
                if (chatCurrentImpressions.size == 10) {
                    sendChatImpressionData()
                }
            }

        } else {
            currentImpressions.add(impressionDataObject)
            sendImpDataForced()
        }
    }

    private fun sendImpDataForced() {
        Monetization.logWtfIfNeeded("sendImpDataForced called")
        if (Monetization.monetizationConfig?.shouldForceSentImpressions == true) {
            sendImpressionData(true)
        }
    }

    private fun logFirebaseEventForAdPaid(context: Context, impressionData: ImpressionData) {

    }

    fun sendImpressionData(
        ignoreIsInAd: Boolean = false,
        completionBlock: (it: ImpressionResponse?) -> Unit = {}
    ) {
        if (isInAd && !ignoreIsInAd) {
            completionBlock(null)
            return
        }

        if (currentImpressions.isEmpty()) {
            completionBlock(null)
            return
        }

        val newList = mutableListOf<ImpressionDataObject>()
        newList.addAll(currentImpressions)

        currentImpressions.clear()

        AttributesNetworkFacade.sendImpressionData(
            newList,
            Monetization.userId,
            {
                completionBlock(it)
            },
            {
                completionBlock(null)
            })
    }

    private fun sendChatImpressionData() {
        if (chatCurrentImpressions.isEmpty()) return

        val newList = mutableListOf<ImpressionDataObject>()
        newList.addAll(chatCurrentImpressions)

        chatCurrentImpressions.clear()

        AttributesNetworkFacade.sendChatImpressionData(
            newList,
            Monetization.userId,
            {
            },
            {
            })
    }

    private fun logFirebaseEventForAdPaid(context: Context?, impressionData: MaxAd?) {
        if (Monetization.monetizationConfig?.shouldLogFirebaseAdEvent != false) {
            impressionData?.let {
                context?.let { it1 ->
                    val firebaseAnalytics = FirebaseAnalytics.getInstance(it1)
                    firebaseAnalytics.logEvent(
                        FirebaseAnalytics.Event.AD_IMPRESSION,
                        Bundle().apply {
                            putString(FirebaseAnalytics.Param.AD_PLATFORM, "appLovin")
                            putString(FirebaseAnalytics.Param.AD_UNIT_NAME, impressionData.adUnitId)
                            putString(
                                FirebaseAnalytics.Param.AD_FORMAT,
                                impressionData.format.displayName
                            )
                            putString(FirebaseAnalytics.Param.AD_SOURCE, impressionData.networkName)
                            putDouble(FirebaseAnalytics.Param.VALUE, impressionData.revenue)
                            putString(
                                FirebaseAnalytics.Param.CURRENCY,
                                "USD"
                            ) // All Applovin revenue is sent in USD
                        })
                }

            }
        }
    }

    internal fun onAdFetched(ad: MaxAd, isWaterfall: Boolean) {
        if (ad == null) {
            Monetization.logWtfIfNeeded("onAdFetched with ad = null")
            return
        }

        var adFormat = FastCashOutAdFormat.NONE

        if (ad.format == MaxAdFormat.INTERSTITIAL) {
            adFormat = FastCashOutAdFormat.INTERSTITIAL
        }

        if (ad.format == MaxAdFormat.REWARDED) {
            adFormat = FastCashOutAdFormat.REWARDED

            when {
                rewardedVideoMaxAd.first == null -> {
                    rewardedVideoMaxAd = Pair(ad, isWaterfall)
                }

                rewardedVideoMaxAdLessCPM.first == null -> {
                    rewardedVideoMaxAd.first?.revenue?.let { interRevenue ->
                        if (interRevenue < ad.revenue) {
                            rewardedVideoMaxAdLessCPM = rewardedVideoMaxAd
                            rewardedVideoMaxAd = Pair(ad, isWaterfall)
                        } else {
                            rewardedVideoMaxAdLessCPM = Pair(ad, isWaterfall)
                        }
                    }
                }

                else -> {
                    rewardedVideoMaxAd = Pair(null, false)
                    rewardedVideoMaxAdLessCPM.first?.revenue?.let { interRevenue ->
                        if (interRevenue < ad.revenue) {
                            rewardedVideoMaxAd = Pair(ad, isWaterfall)
                        } else {
                            rewardedVideoMaxAd = rewardedVideoMaxAdLessCPM
                            rewardedVideoMaxAdLessCPM = Pair(ad, isWaterfall)
                        }
                    }
                }
            }
        }

        handleFastCashOut(ad.revenue, adFormat)
    }

    internal fun onFairBidAdFetched(
        impressionData: ImpressionData?,
        adFormat: FastCashOutAdFormat,
    ) {
        Monetization.logWtfIfNeeded("onFairBidAdFetched ***********")
        if (impressionData == null) {
            Monetization.logWtfIfNeeded("onFairBidAdFetched with impressionData = null")
            return
        }

        handleFastCashOut(impressionData.netPayout, adFormat)
    }

    private fun handleFastCashOut(adRevenue: Double, adFormat: FastCashOutAdFormat) {
        if (Monetization.monetizationConfig?.shouldForceSentImpressions == true) {
            Monetization.logWtfIfNeeded("handleFastCashOut returned because of cycleAds")
            return
        }

        if (Monetization.monetizationConfig?.minimalCpmForSpecialReward == null) {
            Monetization.logWtfIfNeeded("averageCPM?.toDoubleOrNull() = null")
            return
        }

        if (FastCashOutProgressAlertDialog.isInBigRewardWithCashOutAdFlow) {
            if (!FastCashOutProgressAlertDialog.isAlreadyShown) {
                Monetization.onBigRewardWithCashoutProgressCalled(adFormat)
            }
            return
        }

        Monetization.monetizationConfig?.minimalCpmForSpecialReward?.let { minimalCpmForSpecialRewardWrapped ->
            val adCPM = adRevenue * 1000
            if (adCPM >= minimalCpmForSpecialRewardWrapped) {
                Monetization.logWtfIfNeeded("Ad is ${adFormat} and adCPM >= minimalCpmForSpecialReward == $adCPM >= $minimalCpmForSpecialRewardWrapped")

                AttributesNetworkFacade.checkIfEligibleForAdditionalReward(
                    cpm = adCPM,
                    Monetization.userId,
                    success = {
                        when (it.canCashout) {
                            true -> {
                                Monetization.onBigRewardWithCashoutCalled(adFormat, it)
                            }

                            false -> {
                                Monetization.onBigRewardAdCalled(adFormat, it)
                            }
                        }
                    },
                    failure = {
                        Monetization.logWtfIfNeeded("checkIfEligibleForAdditionalReward failed with $it")
                    })

            } else {
                Monetization.logWtfIfNeeded("adCPM < minimalCpmForSpecialReward == $adCPM < $minimalCpmForSpecialRewardWrapped")
            }
        }
    }

    fun getRewardedVideoMaxAd(): MaxAd? {
        return rewardedVideoMaxAd.first ?: rewardedVideoMaxAdLessCPM.first
    }

    fun getInterstitialMaxAdRevenue(): Double {
        val maxWaterfallRevenue = MaxWaterfallInterstitialAdsLoader.getInstance()
            ?.getAdRevenue() ?: 0.0

        val maxRevenue = MaxInterstitialAdsLoader.getInstance()
            ?.getAdRevenue() ?: 0.0

        val maxTopBidRevenue = MaxInterstitialTopBidAdsLoader.getInstance()
            ?.getAdRevenue() ?: 0.0

        val maxMidBidRevenue = MaxInterstitialMidBidAdsLoader.getInstance()
            ?.getAdRevenue() ?: 0.0

        val revenueList =
            listOf<Double>(maxWaterfallRevenue, maxRevenue, maxTopBidRevenue, maxMidBidRevenue)

        return revenueList.max()
    }

    fun getVideoMaxAdRevenue(): Double {
        val maxWaterfallRevenue = MaxWaterfallRewardedAdsLoader.getRevenue()
            ?: 0.0
        val maxRevenue = MaxRewardedAdsLoader?.getRevenue() ?: 0.0

        return when {
            maxWaterfallRevenue > maxRevenue -> {
                maxWaterfallRevenue
            }

            else -> {
                maxRevenue
            }
        }
    }

    fun showAdWithMostRevenue(): GivvyAd {
        //TODO refactor?
        val maxWaterfallRevenue = MaxWaterfallInterstitialAdsLoader.getInstance()
            ?.getAdRevenue()
        val maxRevenue = MaxInterstitialAdsLoader.getInstance()
            ?.getAdRevenue()
        val maxTopBidRevenue = MaxInterstitialTopBidAdsLoader.getInstance()
            ?.getAdRevenue()
        val maxMidBidRevenue = MaxInterstitialMidBidAdsLoader.getInstance()
            ?.getAdRevenue()
        val isReadyMAXWaterfall = MaxWaterfallInterstitialAdsLoader.getInstance()?.hasLoadedAd()
        val isReadyMAXInt = MaxInterstitialAdsLoader.getInstance()?.hasLoadedAd()
        val isReadyMAXTopBidInt = MaxInterstitialTopBidAdsLoader.getInstance()?.hasLoadedAd()
        val isReadyMAXMidBidInt = MaxInterstitialMidBidAdsLoader.getInstance()?.hasLoadedAd()

        Monetization.logWtfIfNeeded("showAdWithMostRevenue maxWaterfallInt{ready = $isReadyMAXWaterfall, rev = $maxWaterfallRevenue} || maxInt{ready = $isReadyMAXInt, rev = $maxRevenue} || maxTopBidInt{ ready = $isReadyMAXTopBidInt, rev = $maxTopBidRevenue} || maxMidBid{ ready = $isReadyMAXMidBidInt, rev = $maxMidBidRevenue}}")

        when {
            isReadyMAXWaterfall == true && isReadyMAXTopBidInt != true && isReadyMAXMidBidInt != true && isReadyMAXInt != true -> {
                Monetization.logWtfIfNeeded("showAdWithMostRevenue maxInt is not ready isReadyMAXInt = $isReadyMAXInt")
                MaxInterstitialAdsLoader.getInstance()?.onAdLoadedButNotReady()
                return MaxWaterfallInterstitialAdsLoader.getInstance()?.showAd() ?: GivvyAd()
            }

            isReadyMAXMidBidInt == true && isReadyMAXTopBidInt != true && isReadyMAXWaterfall != true && isReadyMAXInt != true -> {
                Monetization.logWtfIfNeeded("showAdWithMostRevenue maxInt is not ready isReadyMAXInt = $isReadyMAXInt")
                MaxInterstitialAdsLoader.getInstance()?.onAdLoadedButNotReady()
                MaxWaterfallInterstitialAdsLoader.getInstance()?.onAdLoadedButNotReady()
                return MaxInterstitialMidBidAdsLoader.getInstance()?.showAd() ?: GivvyAd()
            }

            isReadyMAXTopBidInt == true && isReadyMAXMidBidInt != true && isReadyMAXWaterfall != true && isReadyMAXInt != true -> {
                Monetization.logWtfIfNeeded("showAdWithMostRevenue maxInt is not ready isReadyMAXInt = $isReadyMAXInt")
                MaxInterstitialAdsLoader.getInstance()?.onAdLoadedButNotReady()
                MaxWaterfallInterstitialAdsLoader.getInstance()?.onAdLoadedButNotReady()
                return MaxInterstitialTopBidAdsLoader.getInstance()?.showAd() ?: GivvyAd()
            }

            isReadyMAXInt == true && isReadyMAXTopBidInt != true && isReadyMAXMidBidInt != true && isReadyMAXWaterfall != true -> {
                Monetization.logWtfIfNeeded("showAdWithMostRevenue maxInt is not ready isReadyMAXInt = $isReadyMAXInt")
                MaxWaterfallInterstitialAdsLoader.getInstance()?.onAdLoadedButNotReady()
                return MaxInterstitialAdsLoader.getInstance()?.showAd() ?: GivvyAd()
            }

            isReadyMAXInt != true && isReadyMAXWaterfall != true && isReadyMAXMidBidInt != true && isReadyMAXMidBidInt == true -> {
                Monetization.logWtfIfNeeded("showAdWithMostRevenue Both Max Int loaders are not ready, but have revenue, reinstantiating")

                if (MaxInterstitialAdsLoader.getInstance()?.hasLoadedAdInternal() == true) {
                    MaxInterstitialAdsLoader.getInstance()?.onAdLoadedButNotReady()
                }

                if (MaxWaterfallInterstitialAdsLoader.getInstance()
                        ?.hasLoadedAdInternal() == true
                ) {
                    MaxWaterfallInterstitialAdsLoader.getInstance()?.onAdLoadedButNotReady()
                }

                if (MaxInterstitialTopBidAdsLoader.getInstance()?.hasLoadedAdInternal() == true) {
                    MaxInterstitialTopBidAdsLoader.getInstance()?.onAdLoadedButNotReady()
                }

                if (MaxInterstitialMidBidAdsLoader.getInstance()?.hasLoadedAdInternal() == true) {
                    MaxInterstitialMidBidAdsLoader.getInstance()?.onAdLoadedButNotReady()
                }


                if (Monetization.monetizationConfig?.shouldForceSentImpressions == true) {
                    Monetization.logWtfIfNeeded("showAdWithMostRevenue Both Max Int loaders are not ready, but have revenue, showing best video ad")

                    return Monetization.showBestVideoAd()
                }
            }

            (maxWaterfallRevenue == null || maxWaterfallRevenue == 0.0) && (maxTopBidRevenue == null || maxTopBidRevenue == 0.0) && (maxMidBidRevenue == null || maxMidBidRevenue == 0.0) -> {
                Monetization.logWtfIfNeeded("showAdWithMostRevenue maxWaterfallRevenue == null || maxWaterfallRevenue == 0.0")
                return MaxInterstitialAdsLoader.getInstance()?.showAd() ?: GivvyAd()
            }

            (maxRevenue == null || maxRevenue == 0.0) && (maxTopBidRevenue == null || maxTopBidRevenue == 0.0) && (maxMidBidRevenue == null || maxMidBidRevenue == 0.0) -> {
                Monetization.logWtfIfNeeded("showAdWithMostRevenue maxRevenue == null || maxRevenue == 0.0")
                return MaxWaterfallInterstitialAdsLoader.getInstance()?.showAd() ?: GivvyAd()
            }

            (maxWaterfallRevenue == null || maxWaterfallRevenue == 0.0) && (maxRevenue == null || maxRevenue == 0.0) && (maxMidBidRevenue == null || maxMidBidRevenue == 0.0) -> {
                Monetization.logWtfIfNeeded("showAdWithMostRevenue maxWaterfallRevenue == maxRevenue")
                return MaxInterstitialTopBidAdsLoader.getInstance()?.showAd() ?: GivvyAd()
            }

            (maxWaterfallRevenue == null || maxWaterfallRevenue == 0.0) && (maxRevenue == null || maxRevenue == 0.0) && (maxTopBidRevenue == null || maxTopBidRevenue == 0.0) -> {
                Monetization.logWtfIfNeeded("showAdWithMostRevenue maxWaterfallRevenue == maxRevenue")
                return MaxInterstitialMidBidAdsLoader.getInstance()?.showAd() ?: GivvyAd()
            }

            (maxTopBidRevenue ?: 0.0) > (maxRevenue ?: 0.0)
                    && (maxTopBidRevenue ?: 0.0) > (maxWaterfallRevenue ?: 0.0)
                    && (maxTopBidRevenue ?: 0.0) > (maxMidBidRevenue ?: 0.0) -> {
                Monetization.logWtfIfNeeded("showAdWithMostRevenue maxWaterfallRevenue > maxRevenue")
                return MaxInterstitialTopBidAdsLoader.getInstance()?.showAd() ?: GivvyAd()
            }

            (maxMidBidRevenue ?: 0.0) > (maxRevenue ?: 0.0)
                    && (maxMidBidRevenue ?: 0.0) > (maxWaterfallRevenue ?: 0.0)
                    && (maxMidBidRevenue ?: 0.0) > (maxTopBidRevenue ?: 0.0) -> {
                Monetization.logWtfIfNeeded("showAdWithMostRevenue maxWaterfallRevenue > maxRevenue")
                return MaxInterstitialMidBidAdsLoader.getInstance()?.showAd() ?: GivvyAd()
            }

            (maxWaterfallRevenue ?: 0.0) > (maxRevenue ?: 0.0)
                    && (maxWaterfallRevenue ?: 0.0) > (maxMidBidRevenue ?: 0.0)
                    && (maxWaterfallRevenue ?: 0.0) > (maxTopBidRevenue ?: 0.0) -> {
                Monetization.logWtfIfNeeded("showAdWithMostRevenue maxWaterfallRevenue > maxRevenue")
                return MaxWaterfallInterstitialAdsLoader.getInstance()?.showAd() ?: GivvyAd()
            }

            else -> {
                Monetization.logWtfIfNeeded("showAdWithMostRevenue else")
                return MaxInterstitialAdsLoader.getInstance()?.showAd() ?: GivvyAd()
            }
        }

        return GivvyAd()
    }
}