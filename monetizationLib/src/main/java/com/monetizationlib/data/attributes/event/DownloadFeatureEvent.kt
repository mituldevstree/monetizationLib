package com.monetizationlib.data.attributes.event

import com.monetizationlib.data.attributes.model.DownloadStepConfigResponse
import com.monetizationlib.data.attributes.model.StepsResponse
import com.monetizationlib.data.base.model.ViewIntent

sealed class DownloadFeatureEvent : ViewIntent {
    data class SendOnStepActionComplete(
        val currentStep: StepsResponse,
    ) : DownloadFeatureEvent()

    data class OnStepActionComplete(
        val response: DownloadStepConfigResponse,
    ) : DownloadFeatureEvent()

    data class OnStepActionCompleteFailure(val message: String) : DownloadFeatureEvent()

    data class OnCurrentStepChange(
        val currentStep: Int,
        val response: DownloadStepConfigResponse?,
        val currentStepData: StepsResponse?,
    ) : DownloadFeatureEvent()

    data class ResetToDefault(val response: DownloadStepConfigResponse?) : DownloadFeatureEvent()

}