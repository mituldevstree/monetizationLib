package com.monetizationlib.data.attributes.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.monetizationlib.data.ads.fairbid.FairBidAdConfig
import com.monetizationlib.data.ads.ironsource.IronSourceAdConfig
import com.monetizationlib.data.ads.model.AdConfig
import com.monetizationlib.data.ads.model.FastRewardConfig
import com.monetizationlib.data.base.model.entities.RewardLimitationDataConfig

@Keep
data class MonetizationConfig(
    @SerializedName("hideOfferwall") var hideOfferwall: Boolean?,
    @SerializedName("shouldUseApplovinBackfill") var shouldUseApplovinBackfill: Boolean?,
    @SerializedName("adConfig") val adConfig: AdConfig? = null,
    @SerializedName("shouldSkipAdInitialization") val shouldSkipAdInitialization: Boolean?,
    @SerializedName("shouldSkipVPNCheck") val shouldSkipVPNCheck: Boolean?,
    @SerializedName("shouldForceSentImpressions") val shouldForceSentImpressions: Boolean?,
    @SerializedName("shouldCheckForEmulator") val shouldCheckForEmulator: Boolean? = null,
    @SerializedName("shouldBlockOffersForUser") val shouldBlockOffersForUser: Boolean? = null,
    @SerializedName("privacyPolicy") val privacyPolicy: String? = "",
    @SerializedName("shouldLogFirebaseAdEvent") val shouldLogFirebaseAdEvent: Boolean? = true,
    @SerializedName("minimalCpmForSpecialReward") var minimalCpmForSpecialReward: Double = 700.0,
    @SerializedName("fastRewardConfig") var fastRewardConfig: FastRewardConfig? = null,
    @SerializedName("shouldUseFairBid") var shouldUseFairBid: Boolean? = true,
    @SerializedName("fairBidAdConfig") val fairBidAdConfig: FairBidAdConfig? = null,
    @SerializedName("ironSourceAdConfig") val ironSourceAdConfig: IronSourceAdConfig? = null,
    @SerializedName("shouldUseApplovin") var shouldUseApplovin: Boolean? = true,
    @SerializedName("applovinSDKKey") var applovinSDKKey: String? = "",
    @SerializedName("shouldUseIronSourceMediation") var shouldUseIronSourceMediation: Boolean? = false,
    @SerializedName("shouldPreferMeta") var shouldPreferMeta: Boolean? = false,
    @SerializedName("shouldShowNextAdOffer") var shouldShowNextAdOffer: Boolean? = true,
    @SerializedName("coinsForNextAdOffer") var coinsForNextAdOffer: String? = "",
    @SerializedName("descForNextAdOffer") var descForNextAdOffer: String? = "",
    @SerializedName("titleForNextAdOffer") var titleForNextAdOffer: String? = "",
    @SerializedName("redeemTitleForNextAdOffer") var redeemTitleForNextAdOffer: String? = "Congratulations",
    @SerializedName("redeemCoinsForNextAdOffer") var redeemCoinsForNextAdOffer: String? = "",
    @SerializedName("redeemDescForNextAdOffer") var redeemDescForNextAdOffer: String? = "",
    @SerializedName("shouldOpenAppAfterDownload") var shouldOpenAppAfterDownload: Boolean? = true,
    @SerializedName("showDownloadProgressView") private var showDownloadProgressView: Boolean? = true,
    @SerializedName("downloadProgressViewIsCompleted") private var downloadProgressViewIsCompleted: Boolean? = true,
    @SerializedName("downloadProgressViewDesc") var downloadProgressViewDesc: String? = "",
    @SerializedName("downloadProgressViewButtonText") var downloadProgressViewButtonText: String? = "",
    @SerializedName("downloadProgressViewAppsCount") private var downloadProgressViewAppsCount: String? = "" ,
    @SerializedName("downloadProgressViewCurrentAppsCount") private var downloadProgressViewCurrentAppsCount: String? = "" ,
    @SerializedName("downloadProgressViewReward") private val downloadProgressViewReward: String? = "",
    @SerializedName("downloadProgressViewTitle") var downloadProgressViewTitle: String? = "",
    @SerializedName("downloadRedeemTitleForNextAdOffer") var downloadRedeemTitleForNextAdOffer: String? = "",
    @SerializedName("downloadRedeemDescForNextAdOffer") var downloadRedeemDescForNextAdOffer: String? = "",
    @SerializedName("stopApplovinForTimePeriod") var stopApplovinForTimePeriod: Long? = 150_000,
    @SerializedName("maxAdsDisplayFailedAttempts") var maxAdsDisplayFailedAttempts: Int? = 4,
    @SerializedName("rewardLimitationDataConfig") var rewardLimitationDataConfig: RewardLimitationDataConfig? = null,
    @SerializedName("shouldThrowExceptions") var shouldThrowExceptions: Boolean? = false,

    @SerializedName("providersClickModelList")
    var providersClickList: List<ProviderClickModel>? = null,

    @SerializedName("generalClickModel")
    var generalProviderClickModel: ProviderClickModel? = null,
) {
    fun downloadProgressViewReward(): String? {
        return downloadProgressViewReward
    }

    fun showDownloadProgressView(): Boolean? {
        return showDownloadProgressView
    }

    fun downloadProgressViewIsCompleted(): Boolean? {
        return downloadProgressViewIsCompleted
    }

    fun getDownloadProgressViewAppsCount(): String? {
        return downloadProgressViewAppsCount
    }

    fun setDownloadProgressViewAppsCount(appsCount: String?) {
        this.downloadProgressViewAppsCount = appsCount
    }

    fun getDownloadProgressViewCurrentAppsCount(): String? {
        return downloadProgressViewCurrentAppsCount
    }

    fun setDownloadProgressViewCurrentAppsCount(currentAppsCount: String?) {
        this.downloadProgressViewCurrentAppsCount = currentAppsCount
    }
}
