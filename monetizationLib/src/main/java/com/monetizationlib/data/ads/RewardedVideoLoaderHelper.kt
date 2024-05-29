package com.monetizationlib.data.ads

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import com.monetizationlib.data.AdRewardType
import com.monetizationlib.data.FastCashOutAdFormat
import com.monetizationlib.data.Monetization
import com.monetizationlib.data.ads.model.GivvyAd
import com.monetizationlib.data.ads.model.GivvyAdType
import com.monetizationlib.data.base.view.BigRewardAlertDialog
import com.monetizationlib.data.base.view.FastCashOutProgressAlertDialog

@SuppressLint("StaticFieldLeak")
object RewardedVideoLoaderHelper {

    private const val MAX_FAILED_ATTEMPTS = 1

    private var rewardedVideosLoader: MutableList<RewardedVideosLoader> = mutableListOf()

    private var observersList: MutableList<RewardedVideosListener> = mutableListOf()

    private var currentLoaderIndex = 0

    private var loaderFails = 0

    private var activity: Activity? = null

    var isLoading = true

    var hasLoadedAd = false

    @Volatile
    var isInitialized = false

    fun initialize(activity: Activity) {
        if (isInitialized) {
            return
        }

        Monetization.logWtfIfNeeded("RewardedVideoLoaderHelper initialize method is called successfully")

        isInitialized = true

        this.activity = activity

        prepareRewardedLoaders()

        prepareCallbacks()

        rewardedVideosLoader[currentLoaderIndex].loadAd()
    }

    private fun prepareRewardedLoaders() {
        AdProvider.getAdLoader(AdProvider.MAX.getProviderString())?.let {
            rewardedVideosLoader.add(it)
            Monetization.logWtfIfNeeded("RewardedVideoLoaderHelper prepareRewardedLoaders method is called successfully and added provider MAX")
        }

        AdProvider.getAdLoader(AdProvider.MAX_WATERFALL.getProviderString())?.let {
            rewardedVideosLoader.add(it)
            Monetization.logWtfIfNeeded("RewardedVideoLoaderHelper prepareRewardedLoaders method is called successfully and added provider MAX_WATERFALL")
        }
    }

    fun addObserver(observer: RewardedVideosListener) {
        if (observersList.contains(observer)) return

        observersList.add(observer)
    }

    fun removeObserver(observer: RewardedVideosListener) {
        observersList.remove(observer)
    }

    fun notifyObserversAdLoaded() {

        for (rewardedVideosListener in observersList) {
            rewardedVideosListener.onAdLoaded()
        }
    }

    fun notifyObserversRewarded() {
        Monetization.checkAndSetAdRewardTypeValue()

        when {
            BigRewardAlertDialog.isInBigRewardAdFlow -> {
                Monetization.handleBigReward(isWithWithdraw = false)
            }
            FastCashOutProgressAlertDialog.isInBigRewardWithCashOutAdFlow -> {
                Monetization.onBigRewardWithCashoutProgressCalled(FastCashOutAdFormat.REWARDED)
            }
            Monetization.adRewardType == AdRewardType.NEXT_AD_FEATURE -> {

            }
            Monetization.adRewardType == AdRewardType.DOWNLOAD_APPS_FEATURE -> {

            }
            Monetization.adRewardType == AdRewardType.CYCLE_ADS_DOWNLOAD_APPS_FEATURE -> {

            }
            else -> {
                for (rewardedVideosListener in observersList) {
                    rewardedVideosListener.onRewarded()
                }
            }
        }
    }

    fun notifyObserversClosed() {
        for (rewardedVideosListener in observersList) {
            rewardedVideosListener.onAdClosed()
        }
    }

    fun notifyObserversOnAdFailed(adProvider: AdProvider) {
        if (!Monetization.hasLoadedAd()) {
            for (rewardedVideosListener in observersList) {
                rewardedVideosListener.onAdFailed(adProvider)
            }
        }
    }

    private fun notifyObserversOnAdLoad() {
        for (rewardedVideosListener in observersList) {
            rewardedVideosListener.onAdLoad()
        }
    }

    fun playAd(): GivvyAd {
        if (hasLoadedAd) {
            val maxWaterfallInterstitialAdsLoader: MaxWaterfallRewardedAdsLoader? =
                rewardedVideosLoader.firstOrNull {
                    it is MaxWaterfallRewardedAdsLoader
                } as MaxWaterfallRewardedAdsLoader?

            hasLoadedAd = false
            return when {
                maxWaterfallInterstitialAdsLoader != null && maxWaterfallInterstitialAdsLoader.hasLoadedAdReady() == true -> {
                    maxWaterfallInterstitialAdsLoader.playAd()
                }
                else -> {
                    rewardedVideosLoader[currentLoaderIndex].playAd()
                }
            }
        }

        return GivvyAd(givvyAdType = GivvyAdType.None)
    }

    private fun prepareCallbacks() {
        for (rewardedVideosLoader in rewardedVideosLoader) {
            rewardedVideosLoader.setListener(object : RewardedVideosListener {
                override fun onAdLoaded() {
                    isLoading = false

                    hasLoadedAd = true

                    loaderFails = 0

                    notifyObserversAdLoaded()
                }

                override fun onAdFailed(adProvider: AdProvider) {
                    onAdStartedLoading()

                    isLoading = true

                    if (!hasNextLoader(adProvider)) {
                        isLoading = false
                        Monetization.onRewardVideoClosedByUser()
                        notifyObserversOnAdFailed(adProvider)

                        if (loaderFails != MAX_FAILED_ATTEMPTS) {
                            reloadAd()
                            loaderFails++
                        }
                    }
                }

                override fun onRewarded() {
                    isLoading = false
                    notifyObserversRewarded()
                }

                override fun onAdLoad() {
                    onAdStartedLoading()
                }

                override fun onAdClosed() {
                    notifyObserversClosed()
                    Monetization.onRewardVideoClosedByUser()
                    reloadAd()
                }
            })

            rewardedVideosLoader.init(activity)
        }
    }

    fun reloadAd() {
        if (isLoading) return

        Log.e(
            "RewardedVideoLoaderH",
            "reloadAd" + rewardedVideosLoader[currentLoaderIndex].toString() + " cli: " + currentLoaderIndex
        )

        currentLoaderIndex = 0

        rewardedVideosLoader[currentLoaderIndex].loadAd()
    }

    private fun onAdStartedLoading() {
        isLoading = true

        notifyObserversOnAdLoad()
    }

    private fun hasNextLoader(adProvider: AdProvider): Boolean {
        if (currentLoaderIndex >= rewardedVideosLoader.size - 1) {
            return false
        }
        rewardedVideosLoader[++currentLoaderIndex].loadAd()

        return true
    }
}

interface RewardedVideosListener {
    fun onAdLoaded()

    fun onAdFailed(adProvider: AdProvider)

    fun onRewarded()

    fun onAdLoad()

    fun onAdClosed() {}
}

abstract class RewardedVideosLoader {
    var rewardedVideoListener: RewardedVideosListener? = null

    abstract fun init(activity: Activity? = null)

    abstract fun loadAd()

    fun setListener(rewardedVideoListener: RewardedVideosListener) {
        this.rewardedVideoListener = rewardedVideoListener
    }

    abstract fun getTag(): String

    abstract fun playAd(): GivvyAd
}

enum class AdProvider {

    MAX {
        override fun getProviderString() = "MAX"

        override fun getAdLoader() = MaxRewardedAdsLoader
    },
    MAX_WATERFALL {
        override fun getProviderString() = "MAX_WATERFALL"

        override fun getAdLoader() = MaxWaterfallRewardedAdsLoader
    },
    IRONSOURCE {
        override fun getProviderString() = "IronSource"

        override fun getAdLoader() = null
    },
    FAIRBID {
        override fun getProviderString() = "FAIRBID"

        override fun getAdLoader() = null
    };

    abstract fun getProviderString(): String

    abstract fun getAdLoader(): RewardedVideosLoader?

    companion object {
        fun getAdLoader(provider: String): RewardedVideosLoader? {
            for (providerEnum in values()) {
                if (providerEnum.getProviderString() == provider) {
                    return providerEnum.getAdLoader()
                }
            }

            return null
        }
    }
}