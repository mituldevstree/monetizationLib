package com.monetizationlib.data

//import com.monetizationlib.data.service.PackageChangeReceiver
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdViewAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxAdView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.facebook.ads.AudienceNetworkAds
import com.fyber.FairBid
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson
import com.ironsource.mediationsdk.IronSource
import com.monetizationlib.data.BuildConfig
import com.monetizationlib.data.ads.Ads
import com.monetizationlib.data.ads.AppLovinAdsInitializerManager
import com.monetizationlib.data.ads.ConsentHandler
import com.monetizationlib.data.ads.MaxInterstitialAdsLoader
import com.monetizationlib.data.ads.MaxInterstitialMidBidAdsLoader
import com.monetizationlib.data.ads.MaxInterstitialTopBidAdsLoader
import com.monetizationlib.data.ads.MaxRevenueTracker
import com.monetizationlib.data.ads.MaxRewardedAdsLoader
import com.monetizationlib.data.ads.MaxWaterfallInterstitialAdsLoader
import com.monetizationlib.data.ads.MaxWaterfallRewardedAdsLoader
import com.monetizationlib.data.ads.NALAdditional
import com.monetizationlib.data.ads.NALDefault
import com.monetizationlib.data.ads.NALLarge
import com.monetizationlib.data.ads.NALSmall
import com.monetizationlib.data.ads.RewardedVideoLoaderHelper
import com.monetizationlib.data.ads.RewardedVideosListener
import com.monetizationlib.data.ads.TimerUtils
import com.monetizationlib.data.ads.Utility
import com.monetizationlib.data.ads.fairbid.FairBidBannerAdsLoader
import com.monetizationlib.data.ads.fairbid.FairBidInitializerManager
import com.monetizationlib.data.ads.fairbid.FairBidInterstitialAdsLoader
import com.monetizationlib.data.ads.fairbid.FairBidVideoAdsLoader
import com.monetizationlib.data.ads.ironsource.IronSourceInitManager
import com.monetizationlib.data.ads.ironsource.IronSourceInterstitialLoader
import com.monetizationlib.data.ads.ironsource.IronSourceRewardedVideoLoader
import com.monetizationlib.data.ads.model.AdConfig
import com.monetizationlib.data.ads.model.EligibleForRewardResponse
import com.monetizationlib.data.ads.model.GetFastRewardResponse
import com.monetizationlib.data.ads.model.GivvyAd
import com.monetizationlib.data.ads.model.GivvyAdType
import com.monetizationlib.data.ads.model.ImpressionResponse
import com.monetizationlib.data.attributes.businessModule.AttributesBussinesModule
import com.monetizationlib.data.attributes.model.AttributesModel
import com.monetizationlib.data.attributes.model.AttributesNetworkFacade
import com.monetizationlib.data.attributes.model.DownloadStepConfigResponse
import com.monetizationlib.data.attributes.model.EarnedCredits
import com.monetizationlib.data.attributes.model.IpResponse
import com.monetizationlib.data.attributes.model.MonetizationConfig
import com.monetizationlib.data.attributes.model.RewardForNextAdResponse
import com.monetizationlib.data.attributes.model.StepsResponse
import com.monetizationlib.data.attributes.viewModel.AttributesViewModel
import com.monetizationlib.data.attributes.viewModel.DownloadAdsViewModel
import com.monetizationlib.data.base.extensions.ViewUtil.makeAllChildViewHidden
import com.monetizationlib.data.base.extensions.ViewUtil.makeAllChildViewVisible
import com.monetizationlib.data.base.extensions.openAppByPackageName
import com.monetizationlib.data.base.extensions.openWebPage
import com.monetizationlib.data.base.extensions.resetAdIdIntent
import com.monetizationlib.data.base.model.LocaleManager
import com.monetizationlib.data.base.model.entities.RewardLimitationDataConfig
import com.monetizationlib.data.base.model.networkLayer.layerSpecifics.ApiError
import com.monetizationlib.data.base.model.networkLayer.networking.NetworkManager
import com.monetizationlib.data.base.view.*
import com.monetizationlib.data.base.view.bottomsheet.BottomSheetDownloadFeature
import com.monetizationlib.data.base.view.customviews.FontManager
import com.monetizationlib.data.base.view.utility.StepProcessingError
import com.monetizationlib.data.base.viewModel.ResultAsyncState
import com.tapjoy.Tapjoy
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader


@SuppressLint("StaticFieldLeak")
object Monetization {

    var bannerViewGroup: ViewGroup? = null
    var isUsignVpn: Boolean? = false
    var packageName: String = ""
    var localData: IpResponse? = null
    private var isFirstResume = true
    var activity: Activity? = null
    var isInitDone = false

    var isProductionModeEnabled: Boolean = true
    private val observersList: MutableList<com.monetizationlib.data.Observer> = mutableListOf()
    private var downloadFeatureViewModel: DownloadAdsViewModel? = null

    var adConfig: AdConfig? = null
    var monetizationConfig: MonetizationConfig? = null
    internal var adRewardType: AdRewardType = AdRewardType.NONE
    private var downloadFeatureBottomSheet: BottomSheetDownloadFeature? = null
    var isXpApp = false
    var userId: String = ""
    var userEmail: String? = null
    var shouldPrintLogs: Boolean = false
    var shouldMuteAds: Boolean = true
    var needToShowSwipeCheck: Boolean? = false
    var advertId = ""
    private var attemptsToShowAdAboveOtherCounter = 0
    private var downloadPacakgeName: String? = null

    @JvmStatic
    var nativeXmlOne: Int? = null

    @JvmStatic
    var nativeXmlTwo: Int? = null

    @JvmStatic
    var nativeXmlThree: Int? = null

    @JvmStatic
    var nativeXmlFour: Int? = null

    //    private val packageChangeReceiver by lazy { PackageChangeReceiver() }
    fun addRewardedAdsObserver(observer: RewardedVideosListener) {
        RewardedVideoLoaderHelper.addObserver(observer)
    }

    fun removeRewardedAdsObserver(observer: RewardedVideosListener) {
        RewardedVideoLoaderHelper.removeObserver(observer)
    }

    fun getFirstLoader() = Ads.getMediumAdsLoader()

    fun getSecondLoader() = Ads.getLargeAdsLoader()

    fun getThirdLoader() = Ads.getSmallAdsLoader()

    fun getFourthLoader() = Ads.getAdditionalAdsLoader()
    fun showAppInstallAlert(onButtonClick: () -> Unit = {}) {
        AppInstallRewardAlertDialog(context = activity, onButtonClick = {
            try {
                val currentSteps =
                    monetizationConfig?.getDownloadProgressViewCurrentAppsCount()?.toInt()
                val totalSteps = monetizationConfig?.getDownloadProgressViewAppsCount()?.toInt()
                currentSteps?.let {
                    totalSteps?.let {
                        logWtfIfNeeded("showAppInstallAlert currentSteps = $currentSteps < totalSteps = $totalSteps")
                        if (currentSteps < totalSteps) {
                            showBestAd()
                            saveAppLauncherListInPreferences()
                            activity?.getSharedPreferences(
                                packageName, Context.MODE_PRIVATE
                            )?.edit()
                                ?.putInt("adRewardType", AdRewardType.DOWNLOAD_APPS_FEATURE.value)
                                ?.apply()
                        }
                    }
                }
            } catch (exc: Exception) {
                logWtfIfNeeded("showAppInstallAlert exception getDownloadProgressViewCurrentAppsCount = ${monetizationConfig?.getDownloadProgressViewCurrentAppsCount()} *** getDownloadProgressViewAppsCount = ${monetizationConfig?.getDownloadProgressViewAppsCount()}")
            }
            onButtonClick.invoke()
        }).create().show()
    }


    /**
     * Trigger this after user has given consent to toggle icon/flow on app side.
     */
    fun checkIfDownloadFeatureIsAvailable() {
        getDownloadFeatureConfig(activity as FragmentActivity, preCacheResource = true) { config ->
            if (config != null) {
                observersList.forEach {
                    it.onDownloadFeatureAvailable()
                }
            }
        }
    }


    /**
     * Flow to trigger download feature bottom sheet
     */
    fun openDownloadFeature(
        loadingView: ViewGroup,
        onStepProcessingFailure: (StepProcessingError) -> Unit,
        onUserCancel: () -> Unit,
        onRewardClaimed: (EarnedCredits?) -> Unit,
    ) {
        loadingView.makeAllChildViewVisible()
        getDownloadFeatureConfig(
            activity as FragmentActivity,
            preCacheResource = false
        ) { remoteConfig ->
            loadingView.makeAllChildViewHidden()
            if (remoteConfig != null) {
                openDownloadFeatureBottomSheet(
                    remoteConfig, onStepProcessingFailure, onUserCancel, onRewardClaimed
                )
            } else {
                onStepProcessingFailure.invoke(StepProcessingError.API_ERROR.also { value ->
                    value.message =
                        activity?.getString(R.string.download_feature_config_is_currently_unavailable)
                            ?: ""
                })
            }
        }
    }

    private fun openDownloadFeatureBottomSheet(
        downloadConfig: DownloadStepConfigResponse?,
        onStepProcessingFailure: (StepProcessingError) -> Unit,
        onUserCancel: () -> Unit,
        onRewardClaimed: (EarnedCredits?) -> Unit,
    ) {
        activity.let { context ->
            downloadConfig?.let {
                if (downloadFeatureBottomSheet?.dialog?.isShowing == true) {
                    downloadFeatureBottomSheet?.dismiss()
                    downloadFeatureBottomSheet = null
                }
                downloadFeatureBottomSheet =
                    BottomSheetDownloadFeature.newInstance(activity = context as FragmentActivity,
                        actionCallback = object : BottomSheetDownloadFeature.IDialogButtonClick {
                            override fun onNextStep(data: StepsResponse?) {
                                when (data?.type) {
                                    StepsResponse.StepType.PLAY_ADS -> {
                                        if (monetizationConfig?.rewardLimitationDataConfig == null) {
                                            if (hasLoadedAd() || hasLoadedInterstitial()) {
                                                showBestAdForCycleAdsDownloadingAppsFeature()
                                                downloadFeatureBottomSheet?.resetButtonState()
                                            } else {
                                                downloadFeatureBottomSheet?.resetButtonState()
                                                onStepProcessingFailure.invoke(StepProcessingError.AD_NOT_AVAILABLE)
                                            }
                                        } else {
                                            downloadFeatureBottomSheet?.resetButtonState()
                                            onStepProcessingFailure.invoke(StepProcessingError.AD_LIMIT_REACHED)
                                        }

                                    }

                                    StepsResponse.StepType.RESET_AD_ID -> {
                                        activity?.resetAdIdIntent()
                                    }

                                    StepsResponse.StepType.APP_USAGE_TRACKING -> {
                                        activity?.let { context ->
                                            data.downloadedAppToOpen.openAppByPackageName(context)
                                        }
                                    }

                                    StepsResponse.StepType.APP_INSTALL_DURATION -> {
                                        if (data.isReadyToClaim == true) {
                                            downloadFeatureBottomSheet?.goToNextStep()
                                        } else {
                                            activity?.let { context ->
                                                data.downloadedAppToOpen.openAppByPackageName(
                                                    context
                                                )
                                            }
                                        }
                                    }

                                    else -> {

                                    }
                                }
                            }

                            override fun onUserCancel() {
                                downloadFeatureBottomSheet = null
                                onUserCancel.invoke()
                            }

                            override fun onDismiss() {
                                downloadFeatureBottomSheet = null
                            }

                            override fun onFlowCompletion(downloadFeatureConfig: DownloadStepConfigResponse?) {
                                //openDownloadFeatureReward(downloadFeatureConfig, onRewardClaimed)
                                onRewardClaimed.invoke(downloadFeatureConfig?.userReward?.result)
                                downloadFeatureBottomSheet?.dismiss()
                            }

                            override fun onStepProcessingError(errorMessage: StepProcessingError) {
                                onStepProcessingFailure.invoke(errorMessage)
                            }

                        })
                downloadFeatureBottomSheet?.setDownloadConfigDetails(it)
                downloadFeatureBottomSheet?.show(
                    activity as FragmentActivity, BottomSheetDownloadFeature.TAG
                )
                if (downloadPacakgeName != null) {
                    downloadFeatureBottomSheet?.onAppDownloadCompleted(downloadPacakgeName)
                    downloadPacakgeName = null
                }
            }
        }

    }


    /**
     * Trigger is flow needs to be triggered before any other flow and if current step is readyTo be claimed.
     */
    fun isDownloadFeatureReadyToClaimNextStep(
        loadingView: ViewGroup,
        callback: (isReadyForClaim: Boolean, currentStep: StepsResponse.StepType?) -> Unit,
        autoOpenFlow: Boolean,
        onStepProcessingFailure: ((StepProcessingError) -> Unit)? = null,
        onUserCancel: (() -> Unit)? = null,
        onRewardClaimed: ((EarnedCredits?) -> Unit)? = null,
    ) {
        loadingView.makeAllChildViewVisible()
        getDownloadFeatureConfig(
            activity as FragmentActivity,
            preCacheResource = false
        ) { remoteConfig ->
            loadingView.makeAllChildViewHidden()
            val currentStep = remoteConfig?.currentStep ?: 0
            if (currentStep < (remoteConfig?.steps?.size ?: 0)) {
                val currentStepData = remoteConfig?.steps?.get(currentStep)
                if (currentStepData?.isReadyToClaim == true) {
                    if (autoOpenFlow) {
                        if (onStepProcessingFailure != null && onUserCancel != null && onRewardClaimed != null) {
                            openDownloadFeature(
                                loadingView, onStepProcessingFailure, onUserCancel, onRewardClaimed
                            )
                        } else {
                            throw UnsupportedOperationException("autoOpenFlow flag needs all 3 callbacks: onStepProcessingFailure,onUserCancel,onRewardClaimed")
                        }
                    } else {
                        callback.invoke(true, currentStepData.type)
                    }
                } else {
                    callback.invoke(false, currentStepData?.type)
                }
            } else {
                callback.invoke(false, null)
            }
        }

    }


    fun showBestAdForCycleAdsDownloadingAppsFeature() {
        showBestAd()
        saveAppLauncherListInPreferences()
        activity?.getSharedPreferences(
            packageName, Context.MODE_PRIVATE
        )?.edit()?.putInt("adRewardType", AdRewardType.CYCLE_ADS_DOWNLOAD_APPS_FEATURE.value)
            ?.apply()
    }

    private fun startCoroutineTimer(
        delayMillis: Long = 0,
        repeatMillis: Long = 0,
        action: () -> Unit,
    ): Job {
        return GlobalScope.launch {
            delay(delayMillis)
            if (repeatMillis > 0) {
                while (true) {
                    action()
                    delay(repeatMillis)
                }
            } else {
                action()
            }
        }
    }

    /* Should be called when the initialization and screens are hidden */
    fun showConsentFlow(
        activity: Activity,
        maxAdView: MaxAdView? = null,
        withBannerVisibilityManualHandling: Boolean = false,
        success: (() -> Unit)? = null,
    ) {
        var coroutine = GlobalScope.launch { }
        coroutine = startCoroutineTimer(100, 250) {
            if (monetizationConfig != null) {
                Utility.executeOnUIThread {
                    if (monetizationConfig?.shouldUseFairBid == true) {
                        FairBidInitializerManager.getInstance(activity)
                        FairBidInitializerManager.getInstance(activity)
                            ?.showAndHandleConsent(activity, { }) {
                                setupBanner(
                                    activity,
                                    maxAdView,
                                    null,
                                    withBannerVisibilityManualHandling,
                                )
                                success?.invoke()
                            }
                    } else {
                        AppLovinAdsInitializerManager.getInstance(activity)
                            ?.showAndHandleConsent(activity, { }) {
                                setupBanner(
                                    activity,
                                    maxAdView,
                                    null,
                                    withBannerVisibilityManualHandling,
                                )
                                success?.invoke()
                            }
                    }
                }
                coroutine.cancel()
            }
        }
    }

    /* Should be called when the initialization and screens are hidden */
    fun showConsentFlow(
        activity: Activity,
        maxAdView: MaxAdView?,
        withBannerVisibilityManualHandling: Boolean = false,
        success: () -> Unit?,
        onAdsInit: () -> Unit? = {},
    ) {
        var coroutine = GlobalScope.launch { }
        coroutine = startCoroutineTimer(100, 250) {
            if (monetizationConfig != null) {
                Utility.executeOnUIThread {
                    if (monetizationConfig?.shouldUseFairBid == true) {
                        FairBidInitializerManager.getInstance(activity)
                            ?.showAndHandleConsent(activity, onAdsInit) {
                                setupBanner(
                                    activity,
                                    maxAdView,
                                    null,
                                    withBannerVisibilityManualHandling
                                )
                                success()
                            }
                    } else {
                        AppLovinAdsInitializerManager.getInstance(activity)
                            ?.showAndHandleConsent(activity, onAdsInit) {
                                setupBanner(
                                    activity,
                                    maxAdView,
                                    null,
                                    withBannerVisibilityManualHandling
                                )
                                success()
                            }
                    }
                }
                coroutine.cancel()
            }
        }
    }

    fun shouldClickOnAd(context: Context, ad: GivvyAd?): Boolean {
        logWtfIfNeeded("config ${monetizationConfig?.providersClickList}")

        var clickModel = monetizationConfig?.providersClickList?.find {
            it.providerClickModelName.equals(
                ad?.providerName, ignoreCase = true
            )
        }

        logWtfIfNeeded("Ad ${ad?.providerName}")

        if (clickModel == null) {
            clickModel = monetizationConfig?.generalProviderClickModel
        }

        val userClickPercentageBottomRange = clickModel?.clickPercentageBottomRange ?: 25.0
        val userClickPercentageTopRange = clickModel?.clickPercentageTopRange ?: 75.0

        val madeClicks = context.let {
            LocaleManager.getMadeClicks(
                it, clickModel?.providerClickModelName ?: ""
            )
        }

        logWtfIfNeeded("Clicks = Made clicks Provider, $madeClicks ${clickModel?.providerClickModelName}")

        val skippedClicks = context.let {
            LocaleManager.getSkippedClicks(
                it, clickModel?.providerClickModelName ?: ""
            )
        }

        logWtfIfNeeded("Clicks = Skipped clicks Provider, $skippedClicks ${clickModel?.providerClickModelName}")

        val totalClickCount = madeClicks + skippedClicks
        logWtfIfNeeded("Clicks = Total CLick Count, $totalClickCount")

        val clickRatePercentage = (madeClicks.toDouble() / totalClickCount) * 100
        logWtfIfNeeded("Clicks = Click percentage, $clickRatePercentage")

        var shouldSkipClick = false

        if (clickRatePercentage >= userClickPercentageTopRange) {
            shouldSkipClick = true
        } else if (clickRatePercentage <= userClickPercentageBottomRange) {
            shouldSkipClick = true
        } else if (clickRatePercentage in userClickPercentageBottomRange..userClickPercentageTopRange) {
            val depthInRange = clickRatePercentage - userClickPercentageBottomRange
            val depthPercentage =
                (depthInRange / (userClickPercentageTopRange - userClickPercentageBottomRange)) * 100

            logWtfIfNeeded("Clicks = Depth percentage, $depthPercentage")

            shouldSkipClick = (clickModel?.clickPercentageDepth ?: 40) < depthPercentage
        }

        logWtfIfNeeded("Clicks = Should skip click, $shouldSkipClick")

        return shouldSkipClick
    }

    fun setAdClicked(context: Context, ad: GivvyAd?) {
        val clickModel = monetizationConfig?.providersClickList?.find {
            it.providerClickModelName.equals(
                ad?.providerName, ignoreCase = true
            )
        }

        context.let {
            LocaleManager.setClickMade(
                it, clickModel?.providerClickModelName ?: ""
            )
        }

        logWtfIfNeeded("Clicks = Ad click set, model ${clickModel?.providerClickModelName}")
    }

    fun setAdClickSkipped(context: Context, ad: GivvyAd?) {
        val clickModel = monetizationConfig?.providersClickList?.find {
            it.providerClickModelName.equals(
                ad?.providerName, ignoreCase = true
            )
        }

        context.let {
            LocaleManager.setClickSkipped(
                it, clickModel?.providerClickModelName ?: ""
            )
        }

        logWtfIfNeeded("Clicks = Ad click skipped, model ${clickModel?.providerClickModelName}")
    }

    fun hasLoadedAd(): Boolean {
        if (monetizationConfig?.rewardLimitationDataConfig != null) {
            logWtfIfNeeded("hasLoadedAd stopped because rewardLimitationDataConfig = ${monetizationConfig?.rewardLimitationDataConfig}")
            return true
        }

        val shouldUseFairBid = monetizationConfig?.shouldUseFairBid
        val shouldUseApplovin =
            monetizationConfig?.shouldUseApplovin == true || monetizationConfig?.shouldUseApplovinBackfill == true
        val shouldUseIronSourceMediation = monetizationConfig?.shouldUseIronSourceMediation

        return when {
            shouldUseFairBid == true && shouldUseApplovin && shouldUseIronSourceMediation == true -> {
                FairBidVideoAdsLoader.getInstance()
                    ?.isRewardedVideoAvailable() ?: false || RewardedVideoLoaderHelper.hasLoadedAd || IronSourceRewardedVideoLoader.isVideoLoaded()
            }

            shouldUseFairBid == true && shouldUseApplovin -> {
                FairBidVideoAdsLoader.getInstance()
                    ?.isRewardedVideoAvailable() ?: false || RewardedVideoLoaderHelper.hasLoadedAd
            }

            shouldUseFairBid == true && shouldUseIronSourceMediation == true -> {
                FairBidVideoAdsLoader.getInstance()
                    ?.isRewardedVideoAvailable() ?: false || IronSourceRewardedVideoLoader.isVideoLoaded()
            }

            shouldUseApplovin && shouldUseIronSourceMediation == true -> {
                RewardedVideoLoaderHelper.hasLoadedAd || IronSourceRewardedVideoLoader.isVideoLoaded()
            }

            shouldUseFairBid == true -> {
                FairBidVideoAdsLoader.getInstance()?.isRewardedVideoAvailable() ?: false
            }

            shouldUseIronSourceMediation == true -> {
                IronSourceRewardedVideoLoader.isVideoLoaded()
            }

            MaxWaterfallInterstitialAdsLoader.getInstance()?.hasLoadedAd() == true -> {
                RewardedVideoLoaderHelper.hasLoadedAd
            }

            else -> {
                RewardedVideoLoaderHelper.hasLoadedAd
            }
        }
    }

    fun hasLoadedAllAds(): Boolean {
        if (monetizationConfig?.rewardLimitationDataConfig != null) {
            logWtfIfNeeded("hasLoadedAllAds stopped because rewardLimitationDataConfig = ${monetizationConfig?.rewardLimitationDataConfig}")
            return true
        }

        //TODO: We wont use hasApplovinTopBidLoadedAd and hasApplovinMidBidLoadedAd for now
        //If we don`t have an topBid ad set we will consider we have loaded ad
        val hasApplovinTopBidLoadedAd = if (adConfig?.shouldUseAppLovinTopBid == true) {
            MaxInterstitialTopBidAdsLoader.getInstance()?.hasLoadedAd() == true
        } else true

        //If we don`t have an midBid ad set we will consider we have loaded ad
        val hasApplovinMidBidLoadedAd = if (adConfig?.shouldUseAppLovinMidBid == true) {
            MaxInterstitialMidBidAdsLoader.getInstance()?.hasLoadedAd() == true
        } else true


        val checkApplovinAdaptersBlock: () -> Boolean = {
            MaxInterstitialAdsLoader.getInstance()
                ?.hasLoadedAd() == true && MaxWaterfallInterstitialAdsLoader.getInstance()
                ?.hasLoadedAd() == true && MaxRewardedAdsLoader.hasLoadedAd() && MaxWaterfallRewardedAdsLoader.hasLoadedAd()
        }

        val checkFairBidAdaptersBlock: () -> Boolean = {
            FairBidVideoAdsLoader.getInstance()
                ?.hasLoadedAd() == true && FairBidInterstitialAdsLoader.getInstance()
                ?.isAdAvailable() == true
        }

        val checkIronSourceAdaptersBlock: () -> Boolean = {
            IronSourceRewardedVideoLoader.isVideoLoaded() && IronSourceInterstitialLoader.isInterstitialLoaded()
        }

        val hasLoadedAllAds = when {
            monetizationConfig?.shouldUseApplovin == true && monetizationConfig?.shouldUseFairBid == true && monetizationConfig?.shouldUseIronSourceMediation == true -> {
                checkApplovinAdaptersBlock() && checkFairBidAdaptersBlock() && checkIronSourceAdaptersBlock()
            }

            monetizationConfig?.shouldUseApplovin == true && monetizationConfig?.shouldUseFairBid == true -> {
                checkApplovinAdaptersBlock() && checkFairBidAdaptersBlock()
            }

            monetizationConfig?.shouldUseApplovin == true && monetizationConfig?.shouldUseIronSourceMediation == true -> {
                checkApplovinAdaptersBlock() && checkIronSourceAdaptersBlock()
            }

            monetizationConfig?.shouldUseFairBid == true && monetizationConfig?.shouldUseIronSourceMediation == true -> {
                checkFairBidAdaptersBlock() && checkIronSourceAdaptersBlock()
            }

            monetizationConfig?.shouldUseApplovin == true -> {
                checkApplovinAdaptersBlock()
            }

            monetizationConfig?.shouldUseFairBid == true -> {
                checkFairBidAdaptersBlock()
            }

            monetizationConfig?.shouldUseIronSourceMediation == true -> {
                checkIronSourceAdaptersBlock()
            }

            else -> false
        }

        return hasLoadedAllAds
    }

    fun hasLoadedAtLeastThreeAds(): Boolean {
        if (monetizationConfig?.rewardLimitationDataConfig != null) {
            logWtfIfNeeded("hasLoadedAtLeastThreeAds stopped because rewardLimitationDataConfig = ${monetizationConfig?.rewardLimitationDataConfig}")
            return true
        }

        var adsLoaded = 0
        if (MaxInterstitialAdsLoader.getInstance()?.hasLoadedAd() == true) adsLoaded++
        if (MaxInterstitialTopBidAdsLoader.getInstance()?.hasLoadedAd() == true) adsLoaded++
        if (MaxInterstitialMidBidAdsLoader.getInstance()?.hasLoadedAd() == true) adsLoaded++
        if (MaxWaterfallInterstitialAdsLoader.getInstance()?.hasLoadedAd() == true) adsLoaded++
        if (MaxRewardedAdsLoader.hasLoadedAd()) adsLoaded++
        if (MaxWaterfallRewardedAdsLoader.hasLoadedAd()) adsLoaded++
        if (FairBidVideoAdsLoader.getInstance()?.hasLoadedAd() == true) adsLoaded++
        if (FairBidInterstitialAdsLoader.getInstance()?.isAdAvailable() == true) adsLoaded++
        if (IronSourceInterstitialLoader.isInterstitialLoaded()) adsLoaded++
        if (IronSourceRewardedVideoLoader.isVideoLoaded()) adsLoaded++
        logWtfIfNeeded("hasLoadedAtLeastThreeAds adsLoaded = $adsLoaded")
        return adsLoaded >= 3
    }

    private fun hasLoadedAllAdsCheck() {
        var coroutine = GlobalScope.launch { }
        coroutine = startCoroutineTimer(100, 500) {
            Utility.executeOnUIThread {
                if (hasLoadedAllAds()) {
                    notifyAllAdsLoaded()
                    coroutine.cancel()
                }
            }
        }
    }

    private fun hasLoadedAtLeastThreeAdsCheck() {
        var coroutine = GlobalScope.launch { }
        coroutine = startCoroutineTimer(100, 500) {
            Utility.executeOnUIThread {
                if (hasLoadedAtLeastThreeAds()) {
                    notifyThreeAdsLoaded()
                    showNextAdOfferIfPresent()
                    coroutine.cancel()
                }
            }
        }
    }


    fun setupBanner(
        context: Context,
        maxAdView: MaxAdView?,
        bannerViewGroup: ViewGroup?,
        withBannerVisibilityManualHandling: Boolean = false,
    ) {
        this.bannerViewGroup = bannerViewGroup
        val applovinBannerInitCode = {
            maxAdView?.setListener(object : MaxAdViewAdListener {
                override fun onAdLoaded(ad: MaxAd) {
                    if (!withBannerVisibilityManualHandling) {
                        maxAdView.visibility = View.VISIBLE
                        (maxAdView.parent as? ViewGroup)?.visibility = View.VISIBLE
                    }
                }

                override fun onAdLoadFailed(adUnitId: String, error: MaxError) {
                    if (!withBannerVisibilityManualHandling) {
                        maxAdView.visibility = View.GONE
                        (maxAdView.parent as? ViewGroup)?.visibility = View.GONE
                    }
                }

                override fun onAdDisplayed(ad: MaxAd) {
                    if (!withBannerVisibilityManualHandling) {
                        maxAdView.visibility = View.VISIBLE
                        (maxAdView.parent as? ViewGroup)?.visibility = View.VISIBLE
                    }
                }

                override fun onAdHidden(ad: MaxAd) {
                    if (!withBannerVisibilityManualHandling) {
                        maxAdView.visibility = View.GONE
                        (maxAdView.parent as? ViewGroup)?.visibility = View.GONE
                    }
                }

                override fun onAdClicked(ad: MaxAd) {
                }

                override fun onAdDisplayFailed(ad: MaxAd, error: MaxError) {
                }

                override fun onAdExpanded(ad: MaxAd) {
                }

                override fun onAdCollapsed(ad: MaxAd) {
                }
            })

            maxAdView?.loadAd()
            maxAdView?.setRevenueListener(MaxRevenueTracker.getMaxRevenueTracker(context))

            if (!withBannerVisibilityManualHandling) {
                maxAdView?.visibility = View.VISIBLE
                (maxAdView?.parent as? ViewGroup)?.visibility = View.VISIBLE
                maxAdView?.startAutoRefresh()
            }
        }

        val fairbidBannerInitCode = {
            FairBidBannerAdsLoader.getInstance()?.initialize(
                monetizationConfig?.fairBidAdConfig?.bannerAdUnit ?: "534879", bannerViewGroup
            )
        }

        when {
            monetizationConfig?.shouldUseFairBid == true -> {
                fairbidBannerInitCode()
            }

            monetizationConfig?.shouldUseIronSourceMediation == true -> {
                //TODO: IronSource Banner Init and Show
            }

            monetizationConfig?.shouldUseApplovin == true -> {
                applovinBannerInitCode()
            }

            monetizationConfig?.shouldUseIronSourceMediation == true && monetizationConfig?.shouldUseApplovin == true && monetizationConfig?.shouldUseFairBid == true -> {
                fairbidBannerInitCode()
            }

            monetizationConfig?.shouldUseFairBid == true && monetizationConfig?.shouldUseApplovin == true -> {
                fairbidBannerInitCode()
            }

            monetizationConfig?.shouldUseIronSourceMediation == true -> {
                //TODO: IronSource Banner Init and Show
            }

            monetizationConfig?.shouldUseFairBid == true -> {
                fairbidBannerInitCode()
            }

            else -> {
                applovinBannerInitCode()
            }
        }
    }

    /**
     * Method to observe package changes.
     */
//    private fun listenToPackageChanges(activity: FragmentActivity) {
//        val intentFilter = IntentFilter()
//        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED)
//        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED)
//        intentFilter.addDataScheme("package")
//        activity.registerReceiver(packageChangeReceiver, intentFilter)
//    }

    @SuppressLint("StaticFieldLeak")
    fun startInitialization(
        lang: String,
        userId: String,
        email: String = "",
        packageName: String,
        versionName: String,
        activity: FragmentActivity,
        isXpApp: Boolean = false,
        nativeXmlOne: Int? = null,
        nativeXmlTwo: Int? = null,
        nativeXmlThree: Int? = null,
        nativeXmlFour: Int? = null,
        showConsent: (needToShowConsent: Boolean) -> Unit,
        shouldPrintLogs: Boolean = false,
        shouldMuteAds: Boolean = true,
        testDevicesIds: ArrayList<String> = arrayListOf(),
        enableProductionMode: Boolean = true,
    ) {
        if (isInitDone) {
            MaxRevenueTracker.isInAd = false
            return
        }

        this.nativeXmlOne = nativeXmlOne
        this.nativeXmlTwo = nativeXmlTwo
        this.nativeXmlThree = nativeXmlThree
        this.nativeXmlFour = nativeXmlFour
        this.userId = userId
        this.userEmail = email
        this.isXpApp = isXpApp
        this.userId = userId
        this.packageName = packageName
        this.shouldPrintLogs = shouldPrintLogs
        this.shouldMuteAds = shouldMuteAds
        this.activity = activity
        this.isProductionModeEnabled = enableProductionMode

        downloadFeatureViewModel = ViewModelProvider(activity)[DownloadAdsViewModel::class.java]
        logWtfIfNeeded("startInitialization started")

        FontManager.init(activity.applicationContext)

        // Init surveys and offerwalls
        NetworkManager.initialize(
            context = activity,
            lang = lang,
            versionName = versionName,
            packageName = packageName,
            isProductionEnabled = isProductionModeEnabled
        )

        var viewModel = ViewModelProviders.of(activity)[AttributesViewModel::class.java]

        AttributesBussinesModule.getIpAddress().observe(activity, newObserver({
            localData = it
        }))



        viewModel.getConfig(packageName, userId, activity).observe(activity, newObserver({
            logWtfIfNeeded("getConfig is fetched successfully")
            this.monetizationConfig = it
            observersList.forEach { observer ->
                observer.getMonetizationConfig(monetizationConfig)
            }

            AttributesModel.saveConfig(it, activity)
            startInitializationInner(
                it,
                lang,
                userId,
                email,
                packageName,
                versionName,
                activity,
                isXpApp,
                nativeXmlOne,
                nativeXmlTwo,
                nativeXmlThree,
                nativeXmlFour,
                showConsent,
                testDevicesIds = testDevicesIds
            )
        }, onFail = {
            val config = AttributesModel.getOldConfig(activity)
            this.monetizationConfig = config
            logWtfIfNeeded("monetizationConfigFetched, saved config = $config")
            observersList.forEach { observer ->
                observer.getMonetizationConfig(monetizationConfig)
            }

            if (config.adConfig != null) {
                startInitializationInner(
                    config,
                    lang,
                    userId,
                    email,
                    packageName,
                    versionName,
                    activity,
                    isXpApp,
                    nativeXmlOne,
                    nativeXmlTwo,
                    nativeXmlThree,
                    nativeXmlFour,
                    showConsent,
                    testDevicesIds = testDevicesIds
                )
            }
        }))
    }

    private fun getDownloadFeatureConfig(
        activity: FragmentActivity,
        preCacheResource: Boolean = false,
        callbacks: ((DownloadStepConfigResponse?) -> Unit)? = null,
    ) {
        MainScope().launch {
            downloadFeatureViewModel?.getDownloadFeatureConfig(
                packageName, userId, context = activity
            )?.collectLatest {
                if (it != null) {
                    if (preCacheResource) preCacheStepResource(it)
                    callbacks?.invoke(it)
                } else {
                    callbacks?.invoke(null)
                }
            }
        }

    }


    fun startInitializationInner(
        it: MonetizationConfig,
        lang: String,
        userId: String,
        email: String = "",
        packageName: String,
        versionName: String,
        activity: FragmentActivity,
        isXpApp: Boolean = false,
        nativeXmlOne: Int? = null,
        nativeXmlTwo: Int? = null,
        nativeXmlThree: Int? = null,
        nativeXmlFour: Int? = null,
        showConsent: (needToShowConsent: Boolean) -> Unit,
        shouldPrintLogs: Boolean = false,
        testDevicesIds: ArrayList<String> = arrayListOf(),
        bannerViewGroup: ViewGroup? = null,
        enableProductionMode: Boolean = true,
    ) {
        MaxRevenueTracker.isInAd = false
        hasLoadedAllAdsCheck()
        hasLoadedAtLeastThreeAdsCheck()
        checkIfThereIsNewInstalledAppAndHandleIt()

        ConsentHandler.initialize()

        if (it.shouldSkipAdInitialization != true) {
            it.adConfig?.let { it1 ->
                fillAdConfigAds(
                    it1, nativeXmlOne, nativeXmlTwo, nativeXmlThree, nativeXmlFour
                )
            }
        }
        this.adConfig = it.adConfig

        if (it.shouldSkipAdInitialization != true) {
            val shouldUseApplovin =
                monetizationConfig?.shouldUseApplovin == true || monetizationConfig?.shouldUseApplovinBackfill == true
            when {
                monetizationConfig?.shouldUseFairBid == true && shouldUseApplovin && monetizationConfig?.shouldUseIronSourceMediation == true -> {
                    GlobalScope.launch {
                        FairBidInitializerManager.getInstance(activity)?.initialize(
                            activity, userId, it.fairBidAdConfig, bannerViewGroup, showConsent
                        )

                        MaxRevenueTracker.isApplovin = true

                        AppLovinAdsInitializerManager.getInstance(activity)
                            ?.initialize(activity, userId, showConsent, testDevicesIds)

                        IronSourceInitManager.initialize(activity, it.ironSourceAdConfig)
                    }
                }

                monetizationConfig?.shouldUseIronSourceMediation == true && shouldUseApplovin -> {
                    MaxRevenueTracker.isApplovin = true

                    GlobalScope.launch {
                        AppLovinAdsInitializerManager.getInstance(activity)
                            ?.initialize(activity, userId, showConsent, testDevicesIds)

                        IronSourceInitManager.initialize(activity, it.ironSourceAdConfig)
                    }
                }

                monetizationConfig?.shouldUseIronSourceMediation == true && monetizationConfig?.shouldUseFairBid == true -> {
                    GlobalScope.launch {
                        FairBidInitializerManager.getInstance(activity)?.initialize(
                            activity, userId, it.fairBidAdConfig, bannerViewGroup, showConsent
                        )

                        IronSourceInitManager.initialize(activity, it.ironSourceAdConfig)
                    }
                }

                monetizationConfig?.shouldUseFairBid == true && shouldUseApplovin == true -> {
                    MaxRevenueTracker.isApplovin = false

                    GlobalScope.launch {
                        FairBidInitializerManager.getInstance(activity)?.initialize(
                            activity, userId, it.fairBidAdConfig, bannerViewGroup, showConsent
                        )

                        MaxRevenueTracker.isApplovin = true
                        AppLovinAdsInitializerManager.getInstance(activity)
                            ?.initialize(activity, userId, showConsent, testDevicesIds)
                    }
                }

                monetizationConfig?.shouldUseFairBid == true -> {
                    MaxRevenueTracker.isApplovin = false

                    GlobalScope.launch {
                        FairBidInitializerManager.getInstance(activity)?.initialize(
                            activity, userId, it.fairBidAdConfig, bannerViewGroup, showConsent
                        )
                    }
                }

                monetizationConfig?.shouldUseIronSourceMediation == true -> {
                    IronSourceInitManager.initialize(activity, it.ironSourceAdConfig)
                }

                else -> {
                    GlobalScope.launch {
                        MaxRevenueTracker.isApplovin = true

                        AppLovinAdsInitializerManager.getInstance(activity)
                            ?.initialize(activity, userId, showConsent, testDevicesIds)
                    }
                }
            }
        }
        GlobalScope.launch {
            val adId = getAdId(activity)
            var isDeviceRooted = isDeviceRooted()
            Utility.executeOnUIThread {
                var viewModel = ViewModelProviders.of(activity)[AttributesViewModel::class.java]
                viewModel.sendForegroundStatus(Monetization.userId, adId ?: "", isDeviceRooted)
            }
        }
    }


    fun showTestSuite(activity: Activity) {
        FairBid.showTestSuite(activity)
    }

    fun fillAdConfigAds(
        adConfig: AdConfig,
        nativeXmlOne: Int? = null,
        nativeXmlTwo: Int? = null,
        nativeXmlThree: Int? = null,
        nativeXmlFour: Int? = null,
    ) {
        if (nativeXmlTwo != null && adConfig.nativeLargeAdUnit.isNullOrEmpty()) {
            adConfig.nativeLargeAdUnit = adConfig.nativeMediumAdUnit
        }
        if (nativeXmlThree != null && adConfig.nativeSmallAdUnit.isNullOrEmpty()) {
            adConfig.nativeSmallAdUnit = adConfig.nativeMediumAdUnit
        }
        if (nativeXmlFour != null && adConfig.nativeAdditionalOneAdUnit.isNullOrEmpty()) {
            adConfig.nativeAdditionalOneAdUnit = adConfig.nativeMediumAdUnit
        }
    }

    fun onApplicationCreated(context: Context) {
        AudienceNetworkAds.initialize(context)
    }


    fun onPause(activity: Activity) {
//        Tapjoy.onActivityStop(activity)
        IronSource.onPause(activity)
    }

    fun notifyFirstInterstitialLoaded(timeToLoad: Long, waterfallLatency: Long) {
        observersList.forEach {
            it.onFirstInterstitialLoaded(timeToLoad, waterfallLatency)
        }
    }

    fun notifyAllAdsLoaded() {
        observersList.forEach {
            it.onAllAdsLoaded()
        }
    }

    fun onAdShown() {
        observersList.forEach {
            it.onAdShown()
        }
    }

    fun onAdFailedToShow(error: String, errorCode: Int) {
        AttributesNetworkFacade.saveFastWithdrawData(userId, error, {}, {})

        attemptsToShowAdAboveOtherCounter++

        if (errorCode == -23 || error.contains(
                "Attempting to show ad when another fullscreen", true
            )
        ) {
            logWtfIfNeeded("APPLOVIN AD FAILED TO SHOW, BECAUSE OF OTHER APPLOVIN AD. WILL STOP APPLOVIN FOR ${monetizationConfig?.stopApplovinForTimePeriod ?: 150_000} SECONDS")
            monetizationConfig?.shouldUseApplovin = false
            TimerUtils.postDelayedInUIThread({
                logWtfIfNeeded("ENABLE APPLOVIN AGAIN AFTER THE DELAY OF ${monetizationConfig?.stopApplovinForTimePeriod ?: 150_000} SECONDS")
                monetizationConfig?.shouldUseApplovin = true
            }, monetizationConfig?.stopApplovinForTimePeriod ?: 150_000)
        }

        var shouldForceRestart =
            attemptsToShowAdAboveOtherCounter >= (monetizationConfig?.maxAdsDisplayFailedAttempts
                ?: 3)

        val hasLoadedFairBidAd = FairBidVideoAdsLoader.getInstance()
            ?.hasLoadedAd() == true || FairBidInterstitialAdsLoader.getInstance()
            ?.isAdAvailable() == true

        if (!hasLoadedFairBidAd) {
            shouldForceRestart = true
        }

        if (shouldForceRestart) {
            attemptsToShowAdAboveOtherCounter = 0
        }

        logWtfIfNeeded("onAdFailedToShow with shouldForceRestart = $shouldForceRestart")

        observersList.forEach {
            it.onAdFailedToShow(shouldForceRestart = shouldForceRestart)
        }
    }

    fun notifyThreeAdsLoaded() {
        observersList.forEach {
            it.onThreeAdsLoaded()
        }
    }

    fun updateIsInChatFlow(isInChatFlow: Boolean) {
        MaxRevenueTracker.isInChatFlow = isInChatFlow
    }

    fun onShouldForceFinishTheApp() {
        observersList.forEach {
            it.onShouldForceFinishTheApp()
        }
    }

    fun onAdLimitReached(rewardLimitationDataConfig: RewardLimitationDataConfig) {
        observersList.forEach {
            it.onAdLimitReached(rewardLimitationDataConfig)
        }
    }

    fun onResume(activity: FragmentActivity) {
        try {
            IronSource.onResume(activity)
            updateCurrentlySavedActivity(activity)
            checkIfThereIsNewInstalledAppAndHandleIt()

            Tapjoy.onActivityStart(activity)

            if (isUsignVpn == false) {
                isUsignVpn = VPNUtil.isUsingVpn(activity)
            }

            sendForegroundStatusIfNeeded(activity)

        } catch (exc: Exception) {
            logWtfIfNeeded("OnResume Exception - message = ${exc.message} \n exception.stackTrace = ${exc.stackTrace}, ")
        }

        isFirstResume = false
    }

    private fun sendForegroundStatusIfNeeded(activity: FragmentActivity) {
        if (isFirstResume) return
        GlobalScope.launch {
            val oldAdId = this@Monetization.advertId
            val currentAdId = getAdId(activity)

            if (oldAdId.equals(currentAdId, ignoreCase = true)) return@launch

            logWtfIfNeeded("sendForegroundStatusIfNeeded oldAdId != currentAdId =  && oldAdId = $oldAdId && currentAdId = $currentAdId")
            if (userId.isNotEmpty()) {
                Utility.executeOnUIThread {
                    val viewModel =
                        ViewModelProviders.of(activity).get(AttributesViewModel::class.java)
                    viewModel.sendForegroundStatus(
                        userId,
                        currentAdId ?: "",
                        isDeviceRooted(),
                    ).observe(
                        activity, newObserver({
                            it?.let { impResponse ->
                                if (needToShowSwipeCheck == true && impResponse.shouldShowInfoDialog != true) {
                                    showReCaptchaSliderDialogIfPresent(activity, packageName)
                                } else {
                                    if (monetizationConfig?.rewardLimitationDataConfig == null && impResponse.dialogText != null) {
                                        if (impResponse.shouldShowInfoDialog == true) {
                                            impResponse.dialogText.let { message ->
                                                if (activity.isFinishing) return@newObserver
                                                NeutralAlertDialog(context = activity,
                                                    message = message,
                                                    okayButtonTitle = activity.resources.getString(
                                                        R.string.okay
                                                    ),
                                                    showSecondButton = false,
                                                    cancelButtonTitle = "",
                                                    cancelable = false,
                                                    okayAction = {
                                                        if (impResponse.shouldForceClose == true) {
                                                            if (impResponse.appLink.isNullOrEmpty()) {
                                                                activity.finish()
                                                            } else {
                                                                // open link
                                                                impResponse.appLink.openWebPage(
                                                                    activity
                                                                )
                                                                activity.finish()
                                                            }
                                                        }
                                                    }).apply {}.create().show()
                                            }
                                        }
                                    }
                                }
                            }
                        })
                    )
                }
            }
        }
    }

    private fun getAdId(context: Context): String? {
        var idInfo: AdvertisingIdClient.Info? = null
        try {
            idInfo = AdvertisingIdClient.getAdvertisingIdInfo(context)
        } catch (e: GooglePlayServicesNotAvailableException) {
            e.printStackTrace()
        } catch (e: GooglePlayServicesRepairableException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        var advertId: String? = null
        try {
            advertId = idInfo!!.id
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }

        this.advertId = advertId ?: ""
        return advertId
    }

    fun getAdIdQuick(): String {
        return advertId
    }

    fun hasLoadedInterstitial(): Boolean {
        if (monetizationConfig?.rewardLimitationDataConfig != null) {
            logWtfIfNeeded("hasLoadedInterstitial stopped because rewardLimitationDataConfig = ${monetizationConfig?.rewardLimitationDataConfig}")
            return true
        }

        val shouldUseApplovin =
            monetizationConfig?.shouldUseApplovin == true || monetizationConfig?.shouldUseApplovinBackfill == true

        return when {
            monetizationConfig?.shouldUseFairBid == true && shouldUseApplovin && monetizationConfig?.shouldUseIronSourceMediation == true -> {
                FairBidInterstitialAdsLoader.getInstance()
                    ?.isAdAvailable() ?: false || MaxWaterfallInterstitialAdsLoader.getInstance()
                    ?.hasLoadedAdInternal() == true || MaxInterstitialAdsLoader.getInstance()
                    ?.hasLoadedAdInternal() == true || MaxInterstitialTopBidAdsLoader.getInstance()
                    ?.hasLoadedAdInternal() == true || MaxInterstitialMidBidAdsLoader.getInstance()
                    ?.hasLoadedAdInternal() == true || IronSourceInterstitialLoader.isInterstitialLoaded()
            }

            monetizationConfig?.shouldUseFairBid == true && shouldUseApplovin -> {
                FairBidInterstitialAdsLoader.getInstance()
                    ?.isAdAvailable() ?: false || MaxWaterfallInterstitialAdsLoader.getInstance()
                    ?.hasLoadedAdInternal() == true || MaxInterstitialAdsLoader.getInstance()
                    ?.hasLoadedAdInternal() == true || MaxInterstitialTopBidAdsLoader.getInstance()
                    ?.hasLoadedAdInternal() == true || MaxInterstitialMidBidAdsLoader.getInstance()
                    ?.hasLoadedAdInternal() == true
            }

            monetizationConfig?.shouldUseFairBid == true && monetizationConfig?.shouldUseIronSourceMediation == true -> {
                FairBidInterstitialAdsLoader.getInstance()
                    ?.isAdAvailable() ?: false || IronSourceInterstitialLoader.isInterstitialLoaded()
            }

            shouldUseApplovin && monetizationConfig?.shouldUseIronSourceMediation == true -> {
                MaxWaterfallInterstitialAdsLoader.getInstance()
                    ?.hasLoadedAdInternal() == true || MaxInterstitialAdsLoader.getInstance()
                    ?.hasLoadedAdInternal() == true || MaxInterstitialTopBidAdsLoader.getInstance()
                    ?.hasLoadedAdInternal() == true || MaxInterstitialMidBidAdsLoader.getInstance()
                    ?.hasLoadedAdInternal() == true || IronSourceInterstitialLoader.isInterstitialLoaded()
            }

            monetizationConfig?.shouldUseFairBid == true -> {
                FairBidInterstitialAdsLoader.getInstance()?.isAdAvailable() ?: false
            }

            monetizationConfig?.shouldUseIronSourceMediation == true -> {
                IronSourceInterstitialLoader.isInterstitialLoaded()
            }

            else -> {
                MaxWaterfallInterstitialAdsLoader.getInstance()
                    ?.hasLoadedAd() == true || MaxInterstitialAdsLoader.getInstance()
                    ?.hasLoadedAd() == true || MaxInterstitialTopBidAdsLoader.getInstance()
                    ?.hasLoadedAd() == true || MaxInterstitialMidBidAdsLoader.getInstance()
                    ?.hasLoadedAd() == true
            }
        }
    }

    fun hasLoadedInterstitialInternal(): Boolean {

        if (monetizationConfig?.rewardLimitationDataConfig != null) {
            logWtfIfNeeded("hasLoadedInterstitialInternal stopped because rewardLimitationDataConfig = ${monetizationConfig?.rewardLimitationDataConfig}")
            return true
        }

        return when {
            monetizationConfig?.shouldUseFairBid == true && monetizationConfig?.shouldUseApplovin == true && monetizationConfig?.shouldUseIronSourceMediation == true -> {
                FairBidInterstitialAdsLoader.getInstance()
                    ?.isAdAvailable() ?: false || MaxWaterfallInterstitialAdsLoader.getInstance()
                    ?.hasLoadedAdInternal() == true || MaxInterstitialAdsLoader.getInstance()
                    ?.hasLoadedAdInternal() == true || MaxInterstitialTopBidAdsLoader.getInstance()
                    ?.hasLoadedAdInternal() == true || MaxInterstitialMidBidAdsLoader.getInstance()
                    ?.hasLoadedAdInternal() == true || IronSourceInterstitialLoader.isInterstitialLoaded()
            }

            monetizationConfig?.shouldUseFairBid == true && monetizationConfig?.shouldUseApplovin == true -> {
                FairBidInterstitialAdsLoader.getInstance()
                    ?.isAdAvailable() ?: false || MaxWaterfallInterstitialAdsLoader.getInstance()
                    ?.hasLoadedAdInternal() == true || MaxInterstitialAdsLoader.getInstance()
                    ?.hasLoadedAdInternal() == true || MaxInterstitialTopBidAdsLoader.getInstance()
                    ?.hasLoadedAdInternal() == true || MaxInterstitialMidBidAdsLoader.getInstance()
                    ?.hasLoadedAdInternal() == true
            }

            monetizationConfig?.shouldUseFairBid == true && monetizationConfig?.shouldUseIronSourceMediation == true -> {
                FairBidInterstitialAdsLoader.getInstance()
                    ?.isAdAvailable() ?: false || IronSourceInterstitialLoader.isInterstitialLoaded()
            }

            monetizationConfig?.shouldUseApplovin == true && monetizationConfig?.shouldUseIronSourceMediation == true -> {
                MaxWaterfallInterstitialAdsLoader.getInstance()
                    ?.hasLoadedAdInternal() == true || MaxInterstitialAdsLoader.getInstance()
                    ?.hasLoadedAdInternal() == true || MaxInterstitialTopBidAdsLoader.getInstance()
                    ?.hasLoadedAdInternal() == true || MaxInterstitialMidBidAdsLoader.getInstance()
                    ?.hasLoadedAdInternal() == true || IronSourceInterstitialLoader.isInterstitialLoaded()
            }

            monetizationConfig?.shouldUseFairBid == true -> {
                FairBidInterstitialAdsLoader.getInstance()?.isAdAvailable() ?: false
            }

            monetizationConfig?.shouldUseIronSourceMediation == true -> {
                IronSourceInterstitialLoader.isInterstitialLoaded()
            }

            else -> {
                MaxWaterfallInterstitialAdsLoader.getInstance()
                    ?.hasLoadedAd() == true || MaxInterstitialAdsLoader.getInstance()
                    ?.hasLoadedAd() == true || MaxInterstitialTopBidAdsLoader.getInstance()
                    ?.hasLoadedAd() == true || MaxInterstitialMidBidAdsLoader.getInstance()
                    ?.hasLoadedAd() == true
            }
        }
    }

    fun setOnAdLoadedCallback(success: () -> Unit) {
        //TODO this doesn't work for all loaders
        MaxWaterfallInterstitialAdsLoader.setLoadCallback(success)
        MaxInterstitialAdsLoader.setLoadCallback(success)
        MaxInterstitialTopBidAdsLoader.setLoadCallback(success)
        MaxInterstitialMidBidAdsLoader.setLoadCallback(success)
    }

    fun isLoadingRewardedAd(): Boolean {
        return RewardedVideoLoaderHelper.isLoading
    }

    fun isDeviceRooted(): Boolean {
        return checkRootMethod1() || checkRootMethod2() || checkRootMethod3()
    }

    private fun checkRootMethod1(): Boolean {
        val buildTags = Build.TAGS
        return buildTags != null && buildTags.contains("test-keys")
    }

    private fun checkRootMethod2(): Boolean {
        val paths = arrayOf(
            "/system/app/Superuser.apk",
            "/sbin/su",
            "/system/bin/su",
            "/system/xbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su",
            "/data/local/su",
            "/su/bin/su"
        )
        for (path in paths) {
            if (File(path).exists()) return true
        }
        return false
    }

    private fun checkRootMethod3(): Boolean {
        var process: Process? = null
        return try {
            process = Runtime.getRuntime().exec(arrayOf("/system/xbin/which", "su"))
            val `in` = BufferedReader(InputStreamReader(process.inputStream))
            `in`.readLine() != null
        } catch (t: Throwable) {
            false
        } finally {
            process?.destroy()
        }
    }

    private fun isWifi(context: Context): Boolean {
        try {
            val connMgr =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            val wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI)

            val mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)

            return if (wifi!!.isConnectedOrConnecting) {
                true
            } else if (mobile!!.isConnectedOrConnecting) {
                false
            } else {
                false
            }
        } catch (exception: Exception) {
            //Catch wifi permission exception
            return false
        }
    }

    fun onDestroy() {
        getFirstLoader()?.onDestroy()
        getSecondLoader()?.onDestroy()
        getThirdLoader()?.onDestroy()
        getFourthLoader()?.onDestroy()
        NALAdditional.onDestroy()
        NALDefault.onDestroy()
        NALLarge.onDestroy()
        NALSmall.onDestroy()
        FairBidInitializerManager.onDestroy()
        AppLovinAdsInitializerManager.onDestroy()
    }

    fun onStop() {
        logWtfIfNeeded("sendImpressionData onStop")
        val clickJson =
            getActivityOrContext()?.let { LocaleManager.sharedPreferencesToJson(it) } ?: ""

        logWtfIfNeeded("click json : $clickJson")

        MaxRevenueTracker.sendImpressionData()
    }

    fun sendImpressionData(completionBlock: (it: ImpressionResponse?) -> Unit = {}) {
        logWtfIfNeeded("sendImpressionData MONETIZATION")
        MaxRevenueTracker.sendImpressionData(completionBlock = { completionBlock(it) })
    }

    internal fun onInterstitialAdHidden() {
        checkAndSetAdRewardTypeValue()
        when {
            BigRewardAlertDialog.isInBigRewardAdFlow -> {
                handleBigReward(isWithWithdraw = false)
            }

            FastCashOutProgressAlertDialog.isInBigRewardWithCashOutAdFlow -> {
                FastCashOutProgressAlertDialog.isInBigRewardWithCashOutAdFlow = true
                onBigRewardWithCashoutProgressCalled(FastCashOutAdFormat.REWARDED)
            }

            adRewardType == AdRewardType.NEXT_AD_FEATURE -> {

            }

            adRewardType == AdRewardType.DOWNLOAD_APPS_FEATURE -> {

            }

            adRewardType == AdRewardType.CYCLE_ADS_DOWNLOAD_APPS_FEATURE -> {
                downloadFeatureBottomSheet?.resetButtonState()
            }

            else -> {
                observersList.forEach {
                    it.onInterstitialAdHidden()
                }
            }
        }
    }

    internal fun onRewardVideoClosedByUser() {
        if (adRewardType == AdRewardType.CYCLE_ADS_DOWNLOAD_APPS_FEATURE) {
            downloadFeatureBottomSheet?.resetButtonState()
        }
    }

    fun onSurveyDidClose() {
        observersList.forEach {
            it.onSurveyDidClose()
        }
    }

    fun onAppLovinInitializedSuccessfully() {
        observersList.forEach {
            it.onAppLovinInitializedSuccessfully()
        }
    }

    private fun onUserShouldUpdate(fastRewardResponse: GetFastRewardResponse) {
        observersList.forEach {
            it.onUserShouldUpdate(fastRewardResponse = fastRewardResponse)
            (it as? Context)?.let { context ->
                if (fastRewardResponse.earnedCredits > 0) {
                    Toast.makeText(
                        context, (context.getString(R.string.big_reward_toast_text)).format(
                            fastRewardResponse.earnedCredits
                        ), Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun onUpdateUserFromNextAdReward(
        rewardForNextAdResponse: RewardForNextAdResponse,
        packageNameToOpen: String?,
    ) {
        observersList.forEach {
            it.onUpdateUserFromNextAdReward(
                rewardForNextAdResponse = rewardForNextAdResponse, packageNameToOpen
            )
        }
    }

    private fun onCallApiNewAppIsDownloadedCycleAds(
        onSuccess: () -> Unit,
        packageNameToOpen: String?,
    ) {
        var forwardEventToBaseApp = false

        if (adRewardType == AdRewardType.CYCLE_ADS_DOWNLOAD_APPS_FEATURE) {
            Log.e("AppQuery", packageNameToOpen.toString())
            if (downloadFeatureBottomSheet != null) {
                downloadFeatureBottomSheet?.onAppDownloadCompleted(packageNameToOpen)
            } else {
                downloadPacakgeName = packageNameToOpen
                forwardEventToBaseApp = true
            }
        } else {
            forwardEventToBaseApp = true
        }
        if (forwardEventToBaseApp) {
            observersList.forEach {
                logWtfIfNeeded("onCallApiNewAppIsDownloadedCycleAds observer is $it and packageNameToOpen = $packageNameToOpen")
                it.onCallApiNewAppIsDownloadedCycleAds(
                    onSuccess = onSuccess, packageNameToOpen = packageNameToOpen
                )
            }
        }
    }

    fun onBigRewardAdCalled(
        adFormat: FastCashOutAdFormat,
        eligibleForRewardResponse: EligibleForRewardResponse,
    ) {
        observersList.forEach { observer ->
            val context = when (observer) {
                is Context -> observer
                is Fragment -> observer.context
                else -> null
            }
            context?.let { contextWrapped ->
                observer.onBigRewardAdCalled(
                    contextWrapped,
                    adFormat = adFormat,
                    eligibleForRewardResponse = eligibleForRewardResponse
                )
            }
        }
    }

    fun onBigRewardWithCashoutCalled(
        adFormat: FastCashOutAdFormat,
        eligibleForRewardResponse: EligibleForRewardResponse,
    ) {
        observersList.forEach { observer ->
            val context = when (observer) {
                is Context -> observer
                is Fragment -> observer.context
                else -> null
            }
            context?.let { contextWrapped ->
                observer.onBigRewardWithCashoutCalled(
                    contextWrapped,
                    adFormat = adFormat,
                    eligibleForRewardResponse = eligibleForRewardResponse
                )
            }
        }
    }

    fun onBigRewardWithCashoutProgressCalled(
        adFormat: FastCashOutAdFormat,
    ) {
        observersList.forEach { observer ->
            val context = when (observer) {
                is Context -> observer
                is Fragment -> observer.context
                else -> null
            }
            context?.let { contextWrapped ->
                observer.onBigRewardWithCashOutProgressCalled(contextWrapped, adFormat)
            }
        }
    }

    internal fun onFairBidBannerFailedToLoad() {
        observersList.forEach {
            it.onFairBidBannerFailedToLoad()
        }
    }

    internal fun onFairBidBannerLoad() {
        observersList.forEach {
            it.onFairBidBannerLoad()
        }
    }

    fun showReCaptchaSliderDialogIfPresent(
        activity: FragmentActivity,
        packageName: String,
    ) {
        if (this.needToShowSwipeCheck == true && !ReCaptchaAlertDialog.isAlreadyShown) {
            if (activity.isFinishing) return
            ReCaptchaAlertDialog(activity, continueAction = {
                val viewModel = ViewModelProviders.of(activity).get(AttributesViewModel::class.java)
                viewModel.markUserAsReal(packageName, userId).observe(activity, newObserver({}))
            }, shownAction = {}).apply {}.create().show()
        }
    }

    internal fun updateRecaptchaStatus(needToShowSwipeCheck: Boolean?) {
        this.needToShowSwipeCheck = needToShowSwipeCheck
    }

    internal fun updateShowNextAdOffer(shouldShowNextAdOffer: Boolean) {
        logWtfIfNeeded("updateShowNextAdOffer shouldShowNextAdOffer = $shouldShowNextAdOffer ")
        monetizationConfig?.shouldShowNextAdOffer = shouldShowNextAdOffer
        showNextAdOfferIfPresent()
    }

    fun addMonetizationObserver(observer: com.monetizationlib.data.Observer) {
        if (!this.observersList.contains(observer)) this.observersList.add(observer)
    }

    fun removeMonetizationObserver(observer: com.monetizationlib.data.Observer) {
        this.observersList.remove(observer)
    }

    fun getObservers(): MutableList<com.monetizationlib.data.Observer> {
        return this.observersList
    }


    fun logWtfIfNeeded(eventName: String, tagName: String = "MONETIZATION") {
        if (shouldPrintLogs) {
            Log.wtf(tagName, eventName)
        }
    }

    internal fun handleBigReward(isWithWithdraw: Boolean) {
        logWtfIfNeeded("handleBigReward isWithWithdraw = $isWithWithdraw")
        AttributesNetworkFacade.getFastReward(isWithWithdraw = isWithWithdraw, userId, success = {
            onUserShouldUpdate(it)
        }, failure = {
            logWtfIfNeeded("getFastReward fails with ${it}")
        })
        BigRewardAlertDialog.isInBigRewardAdFlow = false
        FastCashOutProgressAlertDialog.isInBigRewardWithCashOutAdFlow = false
    }

    internal fun playInterstitialOrVideo(): GivvyAd {
        if (hasLoadedInterstitial()) {
            return showInterstitial()
        } else if (hasLoadedAd()) {
            return showBestVideoAd()
        }

        return GivvyAd()
    }

    fun showBestAd(): GivvyAd {
        monetizationConfig?.rewardLimitationDataConfig?.let { rewardLimitationDataConfig ->
            onAdLimitReached(rewardLimitationDataConfig)
            return GivvyAd()
        }

        val maxInt = getHighestInterstitialRevenue()
        val maxRewarded = getHighestVideoRevenue()

        logWtfIfNeeded("Max rewarded waterfall: ${MaxWaterfallRewardedAdsLoader.getRevenue()}")
        logWtfIfNeeded("Max rewarded normal: ${MaxRewardedAdsLoader.getRevenue()}")
        logWtfIfNeeded(
            "Fairbid rewarded: ${
                FairBidVideoAdsLoader.getInstance()?.getVideoRevenueData()
            }"
        )
        logWtfIfNeeded(
            "Max interstitial waterfall: ${
                MaxWaterfallInterstitialAdsLoader.getInstance()?.getAdRevenue()
            }"
        )
        logWtfIfNeeded(
            "Max interstitial normal: ${
                MaxInterstitialAdsLoader.getInstance()?.getAdRevenue()
            }"
        )
        logWtfIfNeeded(
            "Max top bid interstitial normal: ${
                MaxInterstitialTopBidAdsLoader.getInstance()?.getAdRevenue()
            }"
        )
        logWtfIfNeeded(
            "Max mid bid interstitial normal: ${
                MaxInterstitialMidBidAdsLoader.getInstance()?.getAdRevenue()
            }"
        )
        logWtfIfNeeded(
            "Fairbid interstitial: ${
                FairBidInterstitialAdsLoader.getInstance()?.getNetPayout()
            }"
        )
        logWtfIfNeeded(
            "IronSource interstitial: ${
                IronSourceInterstitialLoader.getAdRevenue()
            }"
        )
        logWtfIfNeeded(
            "IronSource rewarded video: ${
                IronSourceRewardedVideoLoader.getAdRevenue()
            }"
        )
        logWtfIfNeeded("maxIntRevenue = $maxInt && maxRewardedRevenue = $maxRewarded")

        return if (maxInt >= maxRewarded) {
            showInterstitial()
        } else {
            showBestVideoAd()
        }
    }

    private fun checkForFacebookVideoAdPreference(): GivvyAd {
        if (monetizationConfig?.shouldPreferMeta == true) {
            logWtfIfNeeded("checkForFacebookVideoAdPreference shouldPreferMeta = ${monetizationConfig?.shouldPreferMeta}")
            val fairBidVideoAdsLoader = FairBidVideoAdsLoader.getInstance()
            val fairBidInterstitialAdsLoader = FairBidInterstitialAdsLoader.getInstance()
            val maxInterstitialAdsLoader = MaxInterstitialAdsLoader.getInstance()
            val maxWaterfallInterstitialAdsLoader = MaxWaterfallInterstitialAdsLoader.getInstance()
            val maxTopBidInterstitialAdsLoader = MaxInterstitialTopBidAdsLoader.getInstance()
            val maxMidBidInterstitialAdsLoader = MaxInterstitialMidBidAdsLoader.getInstance()
            val maxRewardedIsFacebookAd =
                MaxRewardedAdsLoader.hasLoadedAd() && MaxRewardedAdsLoader.isFacebookAd()
            val maxWaterfallRewardedIsFacebookAd =
                MaxWaterfallRewardedAdsLoader.hasLoadedAd() && MaxWaterfallRewardedAdsLoader.isFacebookAd()

            return when {
                fairBidVideoAdsLoader?.hasLoadedAd() == true && fairBidVideoAdsLoader.isFacebookAd() -> {
                    fairBidVideoAdsLoader.showAd()
                }

                fairBidInterstitialAdsLoader?.isAdAvailable() == true && fairBidInterstitialAdsLoader.isFacebookAd() -> {
                    fairBidInterstitialAdsLoader.showAd()
                }

                maxInterstitialAdsLoader?.hasLoadedAd() == true && maxInterstitialAdsLoader.isFacebookAd() -> {
                    maxInterstitialAdsLoader.showAd()
                }

                maxWaterfallInterstitialAdsLoader?.hasLoadedAd() == true && maxWaterfallInterstitialAdsLoader.isFacebookAd() -> {
                    maxWaterfallInterstitialAdsLoader.showAd()
                }

                maxTopBidInterstitialAdsLoader?.hasLoadedAd() == true && maxTopBidInterstitialAdsLoader.isFacebookAd() -> {
                    maxTopBidInterstitialAdsLoader.showAd()
                }

                maxMidBidInterstitialAdsLoader?.hasLoadedAd() == true && maxMidBidInterstitialAdsLoader.isFacebookAd() -> {
                    maxMidBidInterstitialAdsLoader.showAd()
                }

                maxRewardedIsFacebookAd -> {
                    MaxRewardedAdsLoader.playAd()
                }

                maxWaterfallRewardedIsFacebookAd -> {
                    MaxWaterfallRewardedAdsLoader.playAd()
                }

                else -> {
                    return GivvyAd(givvyAdType = GivvyAdType.None)
                }
            }
        } else {
            return GivvyAd(givvyAdType = GivvyAdType.None)
        }
    }

    public fun showInterstitial(): GivvyAd {
        logWtfIfNeeded("showInterstitial called")

        monetizationConfig?.rewardLimitationDataConfig?.let { rewardLimitationDataConfig ->
            logWtfIfNeeded("showInterstitial called rewardLimitationDataConfig = $rewardLimitationDataConfig")
            onAdLimitReached(rewardLimitationDataConfig)
            return GivvyAd()
        }

        if (adRewardType != AdRewardType.NONE) {
            setAdsDefaultValuesInSharedPreffs()
        }

        // Check if we should use the loader and if it has a loaded ad
        val shouldUseFairBid =
            monetizationConfig?.shouldUseFairBid == true && FairBidInterstitialAdsLoader.getInstance()
                ?.isAdAvailable() == true
        val shouldUseApplovinWaterfall =
            monetizationConfig?.shouldUseApplovin == true && MaxWaterfallInterstitialAdsLoader.getInstance()
                ?.hasLoadedAd() == true
        val shouldUseApplovin =
            monetizationConfig?.shouldUseApplovin == true && MaxInterstitialAdsLoader.getInstance()
                ?.hasLoadedAd() == true
        val shouldUseApplovinTopBid =
            monetizationConfig?.adConfig?.shouldUseAppLovinTopBid == true && MaxInterstitialTopBidAdsLoader.getInstance()
                ?.hasLoadedAd() == true
        val shouldUseApplovinMidBid =
            monetizationConfig?.adConfig?.shouldUseAppLovinMidBid == true && MaxInterstitialMidBidAdsLoader.getInstance()
                ?.hasLoadedAd() == true
        val shouldUseIronSourceMediation =
            monetizationConfig?.shouldUseIronSourceMediation == true && IronSourceInterstitialLoader.isInterstitialLoaded()

        // Check Max Interstitials for ad loaded but not ready
        MaxWaterfallInterstitialAdsLoader.getInstance()?.getLoadedAd()
        MaxInterstitialAdsLoader.getInstance()?.getLoadedAd()

        // Get revenues
        val revenueDataFairBid = FairBidInterstitialAdsLoader.getInstance()?.getNetPayout() ?: 0.0
        val revenueDataMax = MaxInterstitialAdsLoader.getInstance()?.getAdRevenue() ?: 0.0
        val revenueDataMaxWaterfall =
            MaxWaterfallInterstitialAdsLoader.getInstance()?.getAdRevenue() ?: 0.0
        val revenueDataMaxTopBid =
            MaxInterstitialTopBidAdsLoader.getInstance()?.getAdRevenue() ?: 0.0
        val revenueDataMaxMidBid =
            MaxInterstitialMidBidAdsLoader.getInstance()?.getAdRevenue() ?: 0.0
        val ironSourceRevenue = IronSourceInterstitialLoader.getAdRevenue()

        // Create Pairs of Loader, Revenue
        val fairBidPair = Pair(FairBidInterstitialAdsLoader, revenueDataFairBid)
        val applovinMaxWaterfallPair =
            Pair(MaxWaterfallInterstitialAdsLoader, revenueDataMaxWaterfall)
        val applovinMaxPair = Pair(MaxInterstitialAdsLoader, revenueDataMax)
        val applovinMaxTopBidPair = Pair(MaxInterstitialTopBidAdsLoader, revenueDataMaxTopBid)
        val applovinMaxMidBidPair = Pair(MaxInterstitialMidBidAdsLoader, revenueDataMaxMidBid)
        val ironSourcePair = Pair(IronSourceInterstitialLoader, ironSourceRevenue)

        // Create list of revenues from all loaded ads
        val maxRevenueFromAllLoadedAdsList = mutableListOf<Double>()

        // Create and add all Pairs that will be used
        val pairListOfRevenueAndShowAd = mutableListOf<Pair<Any, Double>>()

        if (shouldUseFairBid) {
            maxRevenueFromAllLoadedAdsList.add(revenueDataFairBid)
            pairListOfRevenueAndShowAd.add(fairBidPair)
        }

        if (shouldUseIronSourceMediation) {
            maxRevenueFromAllLoadedAdsList.add(ironSourceRevenue)
            pairListOfRevenueAndShowAd.add(ironSourcePair)
        }

        if (shouldUseApplovinWaterfall) {
            maxRevenueFromAllLoadedAdsList.add(revenueDataMaxWaterfall)
            pairListOfRevenueAndShowAd.add(applovinMaxWaterfallPair)
        }

        if (shouldUseApplovin) {
            maxRevenueFromAllLoadedAdsList.add(revenueDataMax)
            pairListOfRevenueAndShowAd.add(applovinMaxPair)
        }

        if (shouldUseApplovinTopBid) {
            maxRevenueFromAllLoadedAdsList.add(revenueDataMaxTopBid)
            pairListOfRevenueAndShowAd.add(applovinMaxTopBidPair)
        }

        if (shouldUseApplovinMidBid) {
            maxRevenueFromAllLoadedAdsList.add(revenueDataMaxMidBid)
            pairListOfRevenueAndShowAd.add(applovinMaxMidBidPair)
        }

        //Get the max revenue
        val maxRevenueFromAllLoadedAds = maxRevenueFromAllLoadedAdsList.maxOrNull()
        // Print Pair List
        printPairListToString(pairListOfRevenueAndShowAd, maxRevenueFromAllLoadedAds)

        // Find the pair with max revenue and execute its block - which should be to show the ad
        val maxpair = pairListOfRevenueAndShowAd.firstOrNull { pair: Pair<Any, Double> ->
            pair.second == maxRevenueFromAllLoadedAds
        }

        if (maxpair == null) {
            logWtfIfNeeded("showInterstitial executed with maxpair = null so we are getting the first in the list")
            pairListOfRevenueAndShowAd.firstOrNull()
        }

        logWtfIfNeeded("showInterstitial executed with maxpair = $maxpair")

        return maxpair?.first?.let { showAdBasedOnClass(it) }
            ?: GivvyAd(givvyAdType = GivvyAdType.None)
    }

    public fun showBestVideoAd(): GivvyAd {
        logWtfIfNeeded("showBestVideoAd called")

        monetizationConfig?.rewardLimitationDataConfig?.let { rewardLimitationDataConfig ->
            logWtfIfNeeded("showBestVideoAd called rewardLimitationDataConfig = $rewardLimitationDataConfig")
            onAdLimitReached(rewardLimitationDataConfig)
            return GivvyAd(givvyAdType = GivvyAdType.None)
        }

        if (adRewardType != AdRewardType.NONE) {
            setAdsDefaultValuesInSharedPreffs()
        }

        // Check if we should use the loader and if it has a loaded ad
        val shouldUseFairBidLoader =
            monetizationConfig?.shouldUseFairBid == true && FairBidVideoAdsLoader.getInstance()
                ?.isRewardedVideoAvailable() == true
        val shouldUseApplovinWaterfallLoader =
            monetizationConfig?.shouldUseApplovin == true && MaxWaterfallRewardedAdsLoader.hasLoadedAd()
        val shouldUseApplovinLoader =
            monetizationConfig?.shouldUseApplovin == true && MaxRewardedAdsLoader.hasLoadedAd()
        val shouldUseIronSourceMediation =
            monetizationConfig?.shouldUseIronSourceMediation == true && IronSourceRewardedVideoLoader.isVideoLoaded()

        // Check MAX Rewarded for ad loaded but not ready
        MaxWaterfallRewardedAdsLoader.getLoadedAd()
        MaxRewardedAdsLoader.getLoadedAd()

        // Get revenues
        val revenueDataFairBid = FairBidVideoAdsLoader.getInstance()?.getVideoRevenueData() ?: 0.0
        val revenueDataMax = MaxRewardedAdsLoader.getRevenue()
        val revenueDataMaxWaterfall = MaxWaterfallRewardedAdsLoader.getRevenue()
        val ironSourceRevenue = IronSourceRewardedVideoLoader.getAdRevenue()

        // Create Pairs of Loader, Revenue
        val fairBidPair = Pair(FairBidVideoAdsLoader, revenueDataFairBid)
        val applovinMaxWaterfallPair = Pair(MaxWaterfallRewardedAdsLoader, revenueDataMaxWaterfall)
        val applovinMaxPair = Pair(MaxRewardedAdsLoader, revenueDataMax)
        val ironSourcePair = Pair(IronSourceRewardedVideoLoader, ironSourceRevenue)

        // Create list of revenues from all loaded ads
        val maxRevenueFromAllLoadedAdsList = mutableListOf<Double>()

        // Create and add all Pairs that will be used
        val pairListOfRevenueAndShowAd = mutableListOf<Pair<Any, Double>>()

        if (shouldUseFairBidLoader) {
            maxRevenueFromAllLoadedAdsList.add(revenueDataFairBid)
            pairListOfRevenueAndShowAd.add(fairBidPair)
        }

        if (shouldUseIronSourceMediation) {
            maxRevenueFromAllLoadedAdsList.add(ironSourceRevenue)
            pairListOfRevenueAndShowAd.add(ironSourcePair)
        }

        if (shouldUseApplovinWaterfallLoader) {
            maxRevenueFromAllLoadedAdsList.add(revenueDataMaxWaterfall)
            pairListOfRevenueAndShowAd.add(applovinMaxWaterfallPair)
        }

        if (shouldUseApplovinLoader) {
            maxRevenueFromAllLoadedAdsList.add(revenueDataMax)
            pairListOfRevenueAndShowAd.add(applovinMaxPair)
        }

        val maxRevenueFromAllLoadedAds = maxRevenueFromAllLoadedAdsList.maxOrNull()
        // Print Pair List
        printPairListToString(pairListOfRevenueAndShowAd, maxRevenueFromAllLoadedAds)

        // Find the pair with max revenue and execute its block - which should be to show the ad
        val maxPair = pairListOfRevenueAndShowAd.firstOrNull { pair: Pair<Any, Double> ->
            pair.second == maxRevenueFromAllLoadedAds
        }

        if (maxPair == null) {
            logWtfIfNeeded("showBestVideoAd executed with maxPair = null so we are getting the first in the list")
            pairListOfRevenueAndShowAd.firstOrNull()
        }

        logWtfIfNeeded("showBestVideoAd executed with maxPair = $maxPair")

        return maxPair?.first?.let { showAdBasedOnClass(it) }
            ?: GivvyAd(givvyAdType = GivvyAdType.None)
    }

    public fun showAdBasedOnClass(any: Any): GivvyAd? {
        logWtfIfNeeded("showAdBasedOnClass with Any = $any")
        return when (any) {
            is FairBidVideoAdsLoader.Companion -> FairBidVideoAdsLoader.getInstance()?.showAd()
            is FairBidInterstitialAdsLoader.Companion -> FairBidInterstitialAdsLoader.getInstance()
                ?.showAd()

            is MaxWaterfallRewardedAdsLoader -> MaxWaterfallRewardedAdsLoader.playAd()
            is MaxRewardedAdsLoader -> MaxRewardedAdsLoader.playAd()
            is MaxWaterfallInterstitialAdsLoader.Companion -> MaxWaterfallInterstitialAdsLoader.getInstance()
                ?.showAd()

            is MaxInterstitialAdsLoader.Companion -> MaxInterstitialAdsLoader.getInstance()
                ?.showAd()

            is MaxInterstitialTopBidAdsLoader.Companion -> MaxInterstitialTopBidAdsLoader.getInstance()
                ?.showAd()

            is MaxInterstitialMidBidAdsLoader.Companion -> MaxInterstitialMidBidAdsLoader.getInstance()
                ?.showAd()

            is IronSourceInterstitialLoader -> IronSourceInterstitialLoader.showInterstitial()
            is IronSourceRewardedVideoLoader -> IronSourceRewardedVideoLoader.showVideo()
            else -> {
                if (monetizationConfig?.shouldThrowExceptions == true) {
                    throw Exception("showAdBasedOnClass throws and exception. There is no match to any of the loaders. The Any object that is passed is of type $any")
                }
                return GivvyAd(providerName = "showAdBasedOnClass throws and exception. There is no match to any of the loaders. The Any object that is passed is of type $any")
            }
        }
    }


    private fun printPairListToString(
        pairListOfRevenueAndShowAd: MutableList<Pair<Any, Double>>,
        maxRevenueFromAllLoadedAds: Double?,
    ) {
        var pairListToString = ""
        pairListOfRevenueAndShowAd.forEach {
            pairListToString += "(${it.first.toString()}, ${it.second}), "
        }
        logWtfIfNeeded("printPairListToString with maxRevenueFromAllLoadedAds = $maxRevenueFromAllLoadedAds && pairListToString = $pairListToString")
    }

    fun getHighestInterstitialRevenue(): Double {
        val interstitialImpressionDataFyber =
            FairBidInterstitialAdsLoader.getInstance()?.getInterstitialImpressionData()

        val fairBidAdNetPayout = interstitialImpressionDataFyber?.netPayout
        val interstitialMaxAdRevenue = MaxRevenueTracker.getInterstitialMaxAdRevenue()
        val ironSourceAdRevenue = IronSourceInterstitialLoader.getAdRevenue()

        val revenueList = mutableListOf(interstitialMaxAdRevenue, ironSourceAdRevenue)

        if (fairBidAdNetPayout != null) {
            revenueList.add(fairBidAdNetPayout)
        }

        return revenueList.max()
    }

    fun getHighestVideoRevenue(): Double {
        val revenueDataFairbid = FairBidVideoAdsLoader.getInstance()?.getVideoRevenueData()
        val revenueDataMax = MaxRevenueTracker.getVideoMaxAdRevenue()
        val ironSourceAdRevenue = IronSourceRewardedVideoLoader.getAdRevenue()

        val revenueList = mutableListOf(revenueDataMax, ironSourceAdRevenue)

        if (revenueDataFairbid != null) {
            revenueList.add(revenueDataFairbid)
        }

        return revenueList.max()
    }

    internal fun playVideoOrInterstitial() {
        if (hasLoadedAd()) {
            showBestVideoAd()
        } else if (hasLoadedInterstitial()) {
            showInterstitial()
        }
    }

    private fun saveAppLauncherListInPreferences() {
        val mainIntent = Intent(Intent.ACTION_MAIN, null)
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)
        val pkgAppsList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            activity?.packageManager?.queryIntentActivities(
                mainIntent, PackageManager.ResolveInfoFlags.of(0L)
            )
        } else {
            activity?.packageManager?.queryIntentActivities(mainIntent, 0)
        }

//        logWtfIfNeeded("pkgAppsList.size == ${pkgAppsList?.size} && pkgAppsList == $pkgAppsList")

        val packageNameList = pkgAppsList?.filter { it.activityInfo.packageName.isNotEmpty() }
//        logWtfIfNeeded("packageNameList.size = ${packageNameList?.size}") //&& packageNameList = $packageNameList")
        val packageList = mutableSetOf<String>()
        packageNameList?.forEach {
//            logWtfIfNeeded("packageName = ${it.activityInfo.packageName}")
            packageList.add(it.activityInfo.packageName)
        }


        activity?.getSharedPreferences(packageName, Context.MODE_PRIVATE)?.edit()
            ?.putStringSet("startAppList", packageList)?.apply()
    }

    private fun isThereDifferenceInAppInstalls(): Boolean {
        try {
            val startAppsStringSet =
                activity?.getSharedPreferences(packageName, Context.MODE_PRIVATE)
                    ?.getStringSet("startAppList", null)

            if (startAppsStringSet.isNullOrEmpty()) {
                return false
            }

            val mainIntent = Intent(Intent.ACTION_MAIN, null)
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)
            val pkgAppsList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                activity?.packageManager?.queryIntentActivities(
                    mainIntent, PackageManager.ResolveInfoFlags.of(0L)
                )
            } else {
                activity?.packageManager?.queryIntentActivities(mainIntent, 0)
            }

            val packageNameList = pkgAppsList?.filter { it.activityInfo.packageName.isNotEmpty() }
//        logWtfIfNeeded("packageNameList.size = ${packageNameList?.size}") //&& packageNameList = $packageNameList")
            val packageList = mutableSetOf<String>()
            packageNameList?.forEach {
//            logWtfIfNeeded("packageName = ${it.activityInfo.packageName}")
                packageList.add(it.activityInfo.packageName)
            }

            var allNewApps = listOf<String>()
            startAppsStringSet.let { startAppsStringSetWrapped ->
                allNewApps = packageList.filter { !startAppsStringSetWrapped.contains(it) }
            }

//        logWtfIfNeeded("ONLY NEW APPS == startAppsStringSet.size = ${startAppsStringSet?.size} and allNewApps = $allNewApps")
//        allNewApps.forEach {
//            logWtfIfNeeded("packageName = $it")
//        }

            return allNewApps.isNotEmpty()
        } catch (exception: Exception) {
            val firebaseAnalytics = activity?.let { FirebaseAnalytics.getInstance(it) }
            val params = Bundle().apply {
                putString("packageName", packageName)
                putString("userId", userId)
            }
            firebaseAnalytics?.logEvent("isThereDifferenceInAppInstalls_exception", params)
            return false
        }
    }

    private fun getFirstPackageNameOfInstalledAppOrNull(): String? {
        if (monetizationConfig?.shouldOpenAppAfterDownload == true || (BuildConfig.DEBUG && packageName == "com.givvyvideos")) {
            try {
                val startAppsStringSet =
                    activity?.getSharedPreferences(packageName, Context.MODE_PRIVATE)
                        ?.getStringSet("startAppList", null)

                if (startAppsStringSet.isNullOrEmpty()) {
                    return null
                }

                val mainIntent = Intent(Intent.ACTION_MAIN, null)
                mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)
                val pkgAppsList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    activity?.packageManager?.queryIntentActivities(
                        mainIntent, PackageManager.ResolveInfoFlags.of(0L)
                    )
                } else {
                    activity?.packageManager?.queryIntentActivities(mainIntent, 0)
                }

                val packageNameList =
                    pkgAppsList?.filter { it.activityInfo.packageName.isNotEmpty() }
                val packageList = mutableSetOf<String>()
                packageNameList?.forEach {
                    packageList.add(it.activityInfo.packageName)
                }

                var allNewApps = listOf<String>()
                startAppsStringSet.let { startAppsStringSetWrapped ->
                    allNewApps = packageList.filter { !startAppsStringSetWrapped.contains(it) }
                }

                return allNewApps.firstOrNull()
            } catch (exception: Exception) {
                val firebaseAnalytics = activity?.let { FirebaseAnalytics.getInstance(it) }
                val params = Bundle().apply {
                    putString("packageName", packageName)
                    putString("userId", userId)
                }
                firebaseAnalytics?.logEvent("isThereDifferenceInAppInstalls_exception", params)
                return null
            }
        } else {
            return null
        }
    }

    private fun showNextAdOfferIfPresent() {
        if (monetizationConfig?.shouldForceSentImpressions == true) {
            logWtfIfNeeded("showNextAdOfferIfPresent returned because of cycleAds")
            return
        }
        try {
            logWtfIfNeeded("shouldShowNextAdOffer = ${monetizationConfig?.shouldShowNextAdOffer}")
            if (monetizationConfig?.shouldShowNextAdOffer == true && !NextAdOfferAlertDialog.isShownOnceAlready && hasLoadedAtLeastThreeAds()) {
                getActivityOrContext()?.let { context ->
                    NextAdOfferAlertDialog(context, okayAction = {
                        showBestAd()
                        saveAppLauncherListInPreferences()
                        activity?.getSharedPreferences(
                            packageName, Context.MODE_PRIVATE
                        )?.edit()?.putInt("adRewardType", AdRewardType.NEXT_AD_FEATURE.value)
                            ?.apply()
                    }).apply {}.create().show()
                }
            }
        } catch (exception: Exception) {
            val firebaseAnalytics = activity?.let { FirebaseAnalytics.getInstance(it) }
            val params = Bundle().apply {
                putString("packageName", packageName)
                putString("userId", userId)
            }
            firebaseAnalytics?.logEvent("showNextAdOfferIfPresent_exception", params)
        }
    }

    internal fun showAndGetRewardForNextAd() {
        try {
            if (!NextAdOfferRewardAlertDialog.isAlreadyShown && monetizationConfig?.shouldShowNextAdOffer == true) {
                getActivityOrContext()?.let { context ->
                    NextAdOfferRewardAlertDialog(context = context, okayAction = {
                        (activity as? FragmentActivity)?.let { activity ->
                            downloadFeatureViewModel?.giveRewardForDownloadedApp(
                                packageName = packageName, userId = userId
                            )?.observe(activity, newObserver(onSuccess = {
                                onUpdateUserFromNextAdReward(
                                    it, getFirstPackageNameOfInstalledAppOrNull()
                                )
                                NextAdOfferAlertDialog.isShownOnceAlready = false
                                setAdsDefaultValuesInSharedPreffs()
                            }))
                        }
                    }).create().show()
                }
            }
        } catch (exception: Exception) {
            val firebaseAnalytics = activity?.let { FirebaseAnalytics.getInstance(it) }
            val params = Bundle().apply {
                putString("packageName", packageName)
                putString("userId", userId)
            }
            firebaseAnalytics?.logEvent("showAndGetRewardForNextAd_exception", params)
        }
    }

    private fun setAdsDefaultValuesInSharedPreffs() {
        activity?.getSharedPreferences(
            packageName, Context.MODE_PRIVATE
        )?.edit()?.putStringSet("startAppList", null)?.apply()
        activity?.getSharedPreferences(
            packageName, Context.MODE_PRIVATE
        )?.edit()?.putInt("adRewardType", AdRewardType.NONE.value)?.apply()
        adRewardType = AdRewardType.NONE
    }

    private fun checkIfThereIsNewInstalledAppAndHandleIt() {
        if (isThereDifferenceInAppInstalls()) {
            checkAndSetAdRewardTypeValue()
            if (adRewardType == AdRewardType.NEXT_AD_FEATURE) {
                showAndGetRewardForNextAd()
            }
            if (adRewardType == AdRewardType.DOWNLOAD_APPS_FEATURE) {
                callApiForNewAppDownload()
            }
            if (adRewardType == AdRewardType.CYCLE_ADS_DOWNLOAD_APPS_FEATURE) {
                callApiNewAppIsDownloadedCycleAds()
            }
        }
    }

    private fun callApiForNewAppDownload() {
        NextAdOfferRewardAlertDialog(context = getActivityOrContext(), okayAction = {
            (activity as? FragmentActivity)?.let { activity ->
                downloadFeatureViewModel?.giveRewardForDownloadedApp(
                    packageName = packageName, userId = userId, isForNewAppDownLoad = true
                )?.observe(activity, newObserver(onFail = {
                    logWtfIfNeeded("callApiForNewAppDownload error = ${Gson().toJson(it)}")
                }, onSuccess = {
                    logWtfIfNeeded("callApiForNewAppDownload Success = ${Gson().toJson(it)}")
                    if (it.downloadProgressViewAppsCount.isNullOrEmpty().not()) {
                        monetizationConfig?.setDownloadProgressViewAppsCount(
                            it.downloadProgressViewAppsCount
                        )
                    }
                    if (it.downloadProgressViewCurrentAppsCount.isNullOrEmpty().not()) {
                        monetizationConfig?.setDownloadProgressViewCurrentAppsCount(
                            it.downloadProgressViewCurrentAppsCount
                        )
                    }
                    monetizationConfig?.downloadProgressViewDesc = it.downloadProgressViewDesc
                    monetizationConfig?.downloadProgressViewTitle = it.downloadProgressViewTitle
                    monetizationConfig?.downloadProgressViewButtonText =
                        it.downloadProgressViewButtonText

                    onUpdateUserFromNextAdReward(it, getFirstPackageNameOfInstalledAppOrNull())
                    setAdsDefaultValuesInSharedPreffs()
                }))
            }
        }).create().show()
    }

    private fun callApiNewAppIsDownloadedCycleAds() {
        onCallApiNewAppIsDownloadedCycleAds(onSuccess = {
            logWtfIfNeeded("callApiNewAppIsDownloadedCycleAds Success = ")
            setAdsDefaultValuesInSharedPreffs()
        }, getFirstPackageNameOfInstalledAppOrNull())
    }

    fun updateCurrentlySavedActivity(activity: Activity?) {
        this.activity = activity
    }

    internal fun getActivityOrContext(): Context? {
        try {
            val activityOrContext = if (activity != null && activity?.isDestroyed == false) {
                activity
            } else {
                val observer =
                    observersList.lastOrNull { it is FragmentActivity || it is Activity || it is Context } as? Context
                return observer
            }
            return activityOrContext
        } catch (exc: Exception) {
            val firebaseAnalytics = activity?.let { FirebaseAnalytics.getInstance(it) }
            val params = Bundle().apply {
                putString("packageName", packageName)
                putString("userId", userId)
            }
            firebaseAnalytics?.logEvent("getActivityOrContext_exception", params)
            logWtfIfNeeded("getActivityOrContext_exception stackTrace \n ${exc.stackTrace}")
            return null
        }
    }

    internal fun checkAndSetAdRewardTypeValue() {
        val adRewardTypeInt = activity?.getSharedPreferences(
            packageName, Context.MODE_PRIVATE
        )?.getInt("adRewardType", AdRewardType.NONE.value)
        adRewardTypeInt?.let {
            adRewardType = AdRewardType.fromInt(adRewardTypeInt)
        }
    }

    private fun preCacheStepResource(downloadConfig: DownloadStepConfigResponse) {
        if (downloadConfig.steps.isNotEmpty()) {
            downloadConfig.steps.forEach { stepsResponse ->
                if (stepsResponse.image.isNullOrEmpty().not()) {
                    (activity as FragmentActivity).let {
                        Glide.with(it).load(stepsResponse.image)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .listener(object : RequestListener<Drawable> {
                                override fun onLoadFailed(
                                    e: GlideException?,
                                    model: Any?,
                                    target: Target<Drawable>,
                                    isFirstResource: Boolean,
                                ): Boolean {
                                    Log.e("ImageCache", "Failed")
                                    return true
                                }

                                override fun onResourceReady(
                                    resource: Drawable,
                                    model: Any,
                                    target: Target<Drawable>?,
                                    dataSource: DataSource,
                                    isFirstResource: Boolean,
                                ): Boolean {
                                    Log.e("ImageCache", "Ready")
                                    return true
                                }
                            }).preload(550, 550)

                        Glide.with(it).load(stepsResponse.imageBackground)
                            .diskCacheStrategy(DiskCacheStrategy.ALL).preload(550, 550)

                        Glide.with(it).load(stepsResponse.imageOverlay)
                            .diskCacheStrategy(DiskCacheStrategy.ALL).preload(550, 550)
                    }
                }
            }
        }
    }
}

fun <T> newObserver(
    onSuccess: (T) -> Unit = {},
    onStart: () -> Unit = {},
    onFail: (ApiError) -> Unit = {},
    shouldUseDefaultErrorHandling: Boolean = true,
    withLoading: Boolean = true,
): Observer<ResultAsyncState<T>> {
    return Observer {
        when (it) {
            is ResultAsyncState.Started -> {
                when {
                    withLoading -> {//todo
                    }
                }
                onStart()
            }

            is ResultAsyncState.Success -> {
                when {
                    //todo hide loader
                }
                onSuccess(it.data)
            }

            is ResultAsyncState.Failed -> {
                when {
                    //todo hide loader
                }
                if (shouldUseDefaultErrorHandling) {
                    if (it.apiError.statusCode == -1) {
                        //todo show dialog
                    } else {
                        //todo show dialog
                    }
                }
                onFail(it.apiError)
            }

            is ResultAsyncState.InProgress -> {}
        }
    }

}

fun AppCompatActivity.replaceFragment(
    fragment: Fragment,
    frameId: Int,
    addToBackStack: Boolean = false,
    tag: String? = null,
) {
    supportFragmentManager.inTransaction({
        replace(frameId, fragment, tag)
    }, addToBackStack, tag)
}

fun Fragment.addFragment(
    fragment: Fragment, frameId: Int,
    addToBackStack: Boolean = false,
    tag: String? = null,
) {
    fragmentManager?.inTransaction({
        add(frameId, fragment)
    }, addToBackStack, tag)
}

fun Fragment.replaceFragment(
    fragment: Fragment,
    frameId: Int,
    addToBackStack: Boolean = false,
    tag: String? = null,
) {
    fragmentManager?.inTransaction({
        replace(frameId, fragment, tag)
    }, addToBackStack, tag)
}

inline fun FragmentManager.inTransaction(
    func: FragmentTransaction.() -> Unit,
    addToBackStack: Boolean = false,
    tag: String? = null,
) {
    val fragmentTransaction = beginTransaction()
    fragmentTransaction.func()
    if (addToBackStack) fragmentTransaction.addToBackStack(tag)
    fragmentTransaction.commitAllowingStateLoss()
}

fun View.clickWithDepthEffect(onClick: () -> Unit) {
    this.setOnClickListener {
        animate().scaleX(0.9f).scaleY(0.9f).setDuration(90).withEndAction {
            animate().scaleX(1f).scaleY(1f).setDuration(90).withEndAction {
                onClick.invoke()
            }
        }
    }
}

interface Observer {
    fun onSurveyAvailabilityChange(isAvailable: Boolean) {}
    fun onUserShouldUpdate(fastRewardResponse: GetFastRewardResponse) {}
    fun onUpdateUserFromNextAdReward(
        rewardForNextAdResponse: RewardForNextAdResponse,
        packageNameToOpen: String?,
    ) {
    }

    fun onCallApiNewAppIsDownloadedCycleAds(
        onSuccess: () -> Unit,
        packageNameToOpen: String?,
    ) {
    }

    fun onDownloadFeatureAvailable() {}
    fun onAdShown() {}
    fun onAdFailedToShow(shouldForceRestart: Boolean) {}

    fun onFirstInterstitialLoaded(timeToLoad: Long, waterfallLatency: Long) {}
    fun onInterstitialAdHidden() {}
    fun onSurveyDidClose() {}
    fun getMonetizationConfig(monetizationConfig: MonetizationConfig?) {}
    fun onAppLovinInitializedSuccessfully() {}

    fun getDownloadFeatureVisibility(isVisible: Boolean = false) {}

    fun onBigRewardAdCalled(
        context: Context,
        adFormat: FastCashOutAdFormat,
        eligibleForRewardResponse: EligibleForRewardResponse,
    ) {
        try {
            if (!BigRewardAlertDialog.isAlreadyShown) {
                BigRewardAlertDialog(context, eligibleForRewardResponse, continueAction = {
                    when (adFormat) {
                        FastCashOutAdFormat.INTERSTITIAL -> {
                            BigRewardAlertDialog.isInBigRewardAdFlow = true
                            Monetization.playInterstitialOrVideo()
                        }

                        FastCashOutAdFormat.REWARDED -> {
                            BigRewardAlertDialog.isInBigRewardAdFlow = true
                            Monetization.playVideoOrInterstitial()
                        }

                        else -> {
                            BigRewardAlertDialog.isInBigRewardAdFlow = false
                        }
                    }
                }, shownAction = {}).create().show()
            }
        } catch (exc: Exception) {
            //TODO: Handle an exception with attached context
        }
    }

    fun onBigRewardWithCashoutCalled(
        context: Context,
        adFormat: FastCashOutAdFormat,
        eligibleForRewardResponse: EligibleForRewardResponse,
    ) {
        try {
            if (!FastCashOutAlertDialog.isAlreadyShown && !FastCashOutAlertDialog.isOnceShown) {
                FastCashOutAlertDialog(context,
                    eligibleForRewardResponse,
                    continueAction = { email, provider ->
                        AttributesNetworkFacade.saveFastWithdrawData(provider = provider,
                            email = email,
                            userId = Monetization.userId,
                            success = {
                                Monetization.logWtfIfNeeded("saveFastWithdrawData success")
                                when (adFormat) {
                                    FastCashOutAdFormat.INTERSTITIAL -> {
                                        FastCashOutProgressAlertDialog.isInBigRewardWithCashOutAdFlow =
                                            true
                                        Monetization.playInterstitialOrVideo()
                                    }

                                    FastCashOutAdFormat.REWARDED -> {
                                        FastCashOutProgressAlertDialog.isInBigRewardWithCashOutAdFlow =
                                            true
                                        Monetization.playVideoOrInterstitial()
                                    }

                                    else -> {
                                        FastCashOutProgressAlertDialog.isInBigRewardWithCashOutAdFlow =
                                            false
                                    }
                                }
                            },
                            failure = {
                                Monetization.logWtfIfNeeded("saveFastWithdrawData failure")
                            })
                    }).create().show()
            }
        } catch (exc: Exception) {
            //TODO: Handle an exception with attached context
        }
    }

    fun onBigRewardWithCashOutProgressCalled(
        context: Context,
        adFormat: FastCashOutAdFormat,
    ) {
        try {
            if (!FastCashOutProgressAlertDialog.isAlreadyShown) {
                FastCashOutProgressAlertDialog(context, continueAction = { isFinalStep ->
                    if (isFinalStep) {
                        FastCashOutProgressAlertDialog.isInBigRewardWithCashOutAdFlow = false
                        Monetization.handleBigReward(isWithWithdraw = true)
                    } else {
                        when (adFormat) {
                            FastCashOutAdFormat.INTERSTITIAL -> {
                                FastCashOutProgressAlertDialog.isInBigRewardWithCashOutAdFlow = true
                                Monetization.playInterstitialOrVideo()
                            }

                            FastCashOutAdFormat.REWARDED -> {
                                FastCashOutProgressAlertDialog.isInBigRewardWithCashOutAdFlow = true
                                Monetization.playVideoOrInterstitial()
                            }

                            else -> {
                                FastCashOutProgressAlertDialog.isInBigRewardWithCashOutAdFlow =
                                    false
                            }
                        }
                    }
                }).create().show()
            }
        } catch (exc: Exception) {
            //TODO: Handle an exception with attached context
        }
    }

    fun onFairBidBannerFailedToLoad() {}
    fun onFairBidBannerLoad() {}
    fun onAllAdsLoaded() {}
    fun onThreeAdsLoaded() {}
    fun onShouldForceFinishTheApp() {}
    fun onAdLimitReached(rewardLimitationDataConfig: RewardLimitationDataConfig)
}

enum class FastCashOutAdFormat {
    INTERSTITIAL, REWARDED, NONE
}

enum class AdRewardType(val value: Int) {
    NEXT_AD_FEATURE(0), DOWNLOAD_APPS_FEATURE(1), CYCLE_ADS_DOWNLOAD_APPS_FEATURE(2), NONE(3);

    companion object {
        fun fromInt(value: Int) = AdRewardType.values().first { it.value == value }
    }
}






