package com.monetizationlib.data.attributes.state

import com.monetizationlib.data.attributes.model.DownloadStepConfigResponse
import com.monetizationlib.data.attributes.model.StepsResponse
import com.monetizationlib.data.base.model.ViewState

data class DownloadFeatureUiState(
    var loadingState: Pair<LoadingType, LoadingState> = Pair(
        LoadingType.EMPTY_STATE,
        LoadingState.PROCESSING
    ),
    var config: DownloadStepConfigResponse? = null,
    var errorMessage: Int? = null,
    var currentStepProgress: Int = 0,
    var currentStep: StepsResponse? = null,
) : ViewState {

    companion object {
        val defaultValue = DownloadFeatureUiState(
            loadingState = Pair(
                LoadingType.EMPTY_STATE,
                LoadingState.PROCESSING
            ),
            config = null,
            errorMessage = null
        )

        enum class LoadingState {
            COMPLETED,
            PROCESSING,
            ERROR
        }

        enum class LoadingType {
            RETRIEVE_CONFIG,
            ACTION_COMPLETE,
            ON_STEP_CHANGE,
            EMPTY_STATE
        }
    }


}