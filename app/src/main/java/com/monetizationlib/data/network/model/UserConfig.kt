package com.monetizationlib.data.network.model

import androidx.annotation.Keep
import androidx.databinding.BaseObservable
import com.google.gson.annotations.SerializedName

@Keep
data class UserConfig(
    @SerializedName("serverTimestamp") var serverTimestamp: Long = 0,
    @SerializedName("serverTimezone") var serverTimezone: Long = 0,
    @SerializedName("easiestTutorialIndex") var easiestTutorialIndex: Long = 5L,
    @SerializedName("canShowRatingDialog") var canShowRatingDialog: Boolean = false,
    @SerializedName("coinsForCentExchangeRate") var coinsForCentExchangeRate: Long? = 0,
    @SerializedName("welcomeTextTitle") var welcomeTextTitle: String = "",
    @SerializedName("welcomeTextDescription") var welcomeTextDescription: String = "",
    @SerializedName("needToShowEasiestWinBarTitle") var needToShowEasiestWinBarTitle: String = "",
    @SerializedName("needToShowEasiestWinBarDesc") var needToShowEasiestWinBarDesc: String = "",
    @SerializedName("firstInterstitialIndex") var firstInterstitialIndex: Int = 0,
    @SerializedName("nextInterstitialIndex") var nextInterstitialIndex: Int = 0,
    @SerializedName("canWatchVideoForPresent") var canWatchVideoForPresent: Boolean = false,
    @SerializedName("needToShowEasiestWinBar") var needToShowEasiestWinBar: Boolean = true,
    @SerializedName("totalVideosSecureReward") var totalVideoForSecureReward: Int = 0,
    @SerializedName("coinsSecurrewardPerAd") var rewardAmountPerAd: Long = 0,
    @SerializedName("minWithdrawAmount") var minWithdrawAmount: Double = 0.0,
    @SerializedName("earnUpToInterstitial") private var _earnUpToInterstitial: String = "",
    @SerializedName("earnUpToRewardedVideo") private var _earnUpToRewardVideo: String = "",
    @SerializedName("numberOfAdsForReward") var noOfVideoForSecureReward: Int = 0,
    @SerializedName("offersMaxWin") var maxEarnedByOffer: String = "",
    @SerializedName("dailySecureReward") var dailySecureRewardAmount: String = "",
    @SerializedName("facebookPageLink") var faceBookLink: String? = null,
    @SerializedName("instagramPageLink") var instagramLink: String? = null,
    @SerializedName("givvyEmailId") var givvyEmailId: String? = null,
    @SerializedName("termsAndConditionUrl") var termsAndConditionUrl: String? = null,
    @SerializedName("privacyPolicyUrl") var privacyPolicyUrl: String? = null,
    @SerializedName("countries") var countries: ArrayList<String>? = null,
    @SerializedName("multiplePlayerPeopleInfo") var multiplePlayerPeopleInfo: String = "",
    @SerializedName("contactFormText") val contactUsInfo: String? = null,
    @SerializedName("getCycleAdsRewardForceTime") var getCycleAdsRewardForceTime: Long = 60000,
    var downloadBonusForMiningSession: Int = 0,
    @SerializedName("gcid") var googleClientId: String? = "",
    @SerializedName("downloadsNeededForCyclicAds")
    var overlayPlaybackBonusMaxAppDownload: Int = 2,
    var chargingDeviceBonus: Int = 1,
    var morningWakeUpMessage: String = "",
    var statisticsGraphInfo:String="",
    var offerwallMenuDescription:String="",
    var inviteMenuDescription:String=""
) : BaseObservable()