package com.monetizationlib.data.attributes.model


import android.util.Log
import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class DownloadStepConfigResponse(
    @SerializedName("currentStep") var currentStep: Int? = null,
    @SerializedName("date") var date: String? = null,
    @SerializedName("_id") var id: String? = null,
    @SerializedName("numberOfSteps") var numberOfSteps: Int? = null,
    @SerializedName("packageName") var packageName: String? = null,
    @SerializedName("userId") var userId: String? = null,
    @SerializedName("steps") var steps: ArrayList<StepsResponse> = ArrayList(),
    @SerializedName("reward") var claimedReward: Long = 0L,
    @SerializedName("isCompleted") var isRewardClaimed: Boolean = false,
    @SerializedName("themeConfig")
    var themeConfig: ThemeConfig? = null,
    @SerializedName("userInfo")
    var userReward: UserInfo? = null,
) {
    fun getCurrentActiveStep(): Int {
        return if (steps.isEmpty().not()) {
            val completedLastStep = steps.indexOfLast { it.isCompleted == true }

            if (completedLastStep.plus(1) > steps.size) {
                Log.e("CompletedIndex", completedLastStep.toString())
                completedLastStep
            } else {
                Log.e("CompletedIndex", completedLastStep.plus(1).toString())
                completedLastStep.plus(1)
            }
        } else {
            0
        }
    }

    fun updateCurrentState(
        remoteData: DownloadStepConfigResponse?,
    ) {
        this.isRewardClaimed = remoteData?.isRewardClaimed ?: false
        this.currentStep = remoteData?.currentStep
        if (remoteData?.userReward != null)
            this.userReward = remoteData.userReward
        remoteData?.steps?.let { steps ->
            this.steps = steps
        }
    }

    inner class UserInfo {
        val result: EarnedCredits? = null
    }

}