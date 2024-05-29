package com.monetizationlib.data.ads.ironsource

import android.app.Activity
import com.ironsource.mediationsdk.IronSource
import com.ironsource.mediationsdk.integration.IntegrationHelper
import com.monetizationlib.data.Monetization

object IronSourceInitManager {

    private var isInitialized = false
    fun initialize(activity: Activity, config: IronSourceAdConfig?) {
        Monetization.logWtfIfNeeded("IronSource initialize with userId = ${Monetization.userId} && appId = ${config?.appId} ")

        // UserId should be set before initialization
        IronSource.setUserId(Monetization.userId)

        IronSource.setLevelPlayRewardedVideoManualListener(IronSourceRewardedVideoLoader)
        IronSource.setLevelPlayInterstitialListener(IronSourceInterstitialLoader)

        config?.appId?.let {
            // For testing we can use this appId = "1e5ae7855"
            IronSource.init(activity, config.appId) {
                isInitialized = true
                Monetization.logWtfIfNeeded("IronSource init complete")
                IronSourceRewardedVideoLoader.loadRewardedVideo()
                IronSourceInterstitialLoader.loadInterstitial()
//            IronSource.loadBanner()
            }

            IntegrationHelper.validateIntegration(activity);
        }
    }
}