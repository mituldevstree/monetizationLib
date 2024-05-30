package com.monetizationlib.data.network.model

import androidx.annotation.Keep
import androidx.databinding.BaseObservable
import com.google.gson.annotations.SerializedName

@Keep
data class User(
    @SerializedName(value = "id", alternate = ["_id", "friendId"]) var id: String? = null,
    @SerializedName(
        value = "currentCoins", alternate = ["credits", "userCredits"]
    ) private var _coinBalance: Double = 0.0,
    @SerializedName(
        value = "photo", alternate = ["playerProfile", "userPhoto"]
    ) private var _image: String? = null,
    @SerializedName("showWithdrawShareReminder") var showWithdrawShareReminder: Boolean = false,
    @SerializedName("referralCode") var referralCode: String? = null,
    @SerializedName("externalAuthToken") var externalAuthToken: String? = null,
    @SerializedName("needToMakeSocialLogin") var needToMakeSocialLogin: Boolean = true,
    @SerializedName("externalId") var externalUserID: String? = null,
    @SerializedName("name")  var _username: String? = null,
    @SerializedName("email") var _email: String? = null,
    @SerializedName("country", alternate = ["userCountry"]) private var _country: String? = null,
    @SerializedName("loginType") var _loginType: String? = null,
    @SerializedName("referralLink") var referralLink: String? = null,
    @SerializedName("currency") var currency: String? = "$",
    @SerializedName("userBalance") var _userBalance: Float = 0f,
    @SerializedName("userBalanceDouble") var _userBalanceDouble: Float = 0f,
    @SerializedName("referralTextInfo") val referralTextInfo: String? = "",
    @SerializedName("earnCredits") private var _coinsWon: Double = 0.0,
    @SerializedName("percentOfMinCashOut") var cashOutPercent: Float = 0f,
    @SerializedName("watchVideoForPresent") private var _bonusRewardProgress: Int = 0,
    var externalSessionCode: String? = null,

    @SerializedName("shouldShowCycleAds") var shouldShowCycleAds: Boolean = true,
    @SerializedName("maxTryForAds") var maxTryForAds: Int = 20,
    @SerializedName("appWillReopenAfterTimeDecrement") var appWillReopenAfterTimeDecrement: Int = 31000,
    @SerializedName("clickPercentage") var clickPercentage: Int? = 0,
    @SerializedName("maxAdLoadAttempts") var maxAdLoadAttempts: Int? = 0,
    @SerializedName("firstAdsLoadsWaitTime") var firstAdsLoadsWaitTime: Long? = 30000,
    @SerializedName("videoAdsWaitTimeForClickAndOpen") var videoAdsWaitTimeForClickAndOpen: Long? = 30000,
    @SerializedName("waitTimeForTheAdsClick") var waitTimeForTheAdsClick: Long = 2000,
    @SerializedName("waitTimeForTheReopenApp") var waitTimeForTheReopenApp: Long = 10000,
    @SerializedName("maxBonusEarningPercentagePerPool") var maxBonusEarningPercentagePerPool: Int = 30,
    @SerializedName("shouldShowBigReward") var shouldShowBigReward: Boolean = false,
    @SerializedName("canUseTransparentOverlay") var canUseTransparentOverlay: Boolean = false,
    @Transient var dailyEarningLimitReached: Boolean = false,
    @SerializedName("currentProgressForCylicAds")
    private var _overlayPlaybackBonusDownloadProgress: Int = 0,
) : BaseObservable()