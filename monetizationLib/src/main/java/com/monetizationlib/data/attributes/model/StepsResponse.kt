package com.monetizationlib.data.attributes.model


import android.util.Log
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.monetizationlib.data.base.extensions.convertToHHMMSS
import kotlin.math.abs

@Keep
data class StepsResponse(
    @SerializedName("buttonText") var buttonText: String? = null,
    @SerializedName("date") var date: String? = null,
    @SerializedName("desc") var desc: String? = null,
    @SerializedName("hasTimer") var hasTimer: Boolean? = null,
    @SerializedName("image") var image: String? = null,
    @SerializedName("imageOverlay") var imageOverlay: String? = null,
    @SerializedName("imageBackground") var imageBackground: String? = null,
    @SerializedName("isButtonActive")
    var isButtonActive: Boolean? = null,
    @SerializedName("isCompleted")
    var isCompleted: Boolean? = null,
    @SerializedName("isReadyToClaim")
    var isReadyToClaim: Boolean? = null,
    @SerializedName("reward")
    var reward: String? = null,
    @SerializedName("subTitle")
    var subTitle: String? = null,
    @SerializedName("timeLeftInSeconds")
    var timeLeftInSeconds: Double? = null,
    @SerializedName("title")
    var title: String? = null,
    @SerializedName("type")
    var type: StepType = StepType.INTRO,
    @SerializedName("step")
    var step: Int? = null,
    @SerializedName("downloadedApp")
    var downloadedAppToOpen: String? = null,
    @Transient
    var hasOpenedDownloadedApp: Boolean = false,
    var adId: String? = null,
    var timeSpentInApp: Long = 0L,
    var appInstallDuration: Long = 0L,
) {


    enum class StepType(val remoteName: String) {
        @SerializedName("intro")
        INTRO("intro"),

        @SerializedName("playAd")
        PLAY_ADS("playAd"),

        @SerializedName("stayInApp")
        APP_USAGE_TRACKING("stayInApp"),

        @SerializedName("keepTheApp")
        APP_INSTALL_DURATION("keepTheApp"),

        @SerializedName("claimReward")
        FINAL("claimReward"),

        @SerializedName("resetAdId")
        RESET_AD_ID("resetAdId"),

    }

    fun getRemainingAppUsageTime(): Long {
        val remainingTime = timeLeftInSeconds?.toInt()?.minus(timeSpentInApp) ?: 0L
        Log.e("RemainingTime", remainingTime.toString().plus(" ").plus(timeSpentInApp))
        return if (remainingTime < 0) 0L else remainingTime
    }

    /* fun getRemainingInstallDurationTime(): Long {
         val remainingTime = timeLeftInSeconds?.toInt()?.minus(appInstallDuration) ?: 0L
         Log.e("RemainingTimeInstall", remainingTime.toString())
         return if (remainingTime < 0) 0L else remainingTime
     }*/

    fun getFormattedRemainingTime(): String {
        return getRemainingAppUsageTime().convertToHHMMSS()
    }

    fun needToShowStepProgress(): Boolean {
        return type != StepType.FINAL
    }
}