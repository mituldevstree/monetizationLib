package com.monetizationlib.data.attributes.viewModel

import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.monetizationlib.data.Monetization
import com.monetizationlib.data.attributes.businessModule.AttributesBussinesModule
import com.monetizationlib.data.attributes.event.DownloadFeatureEvent
import com.monetizationlib.data.attributes.model.DownloadStepConfigResponse
import com.monetizationlib.data.attributes.model.StepsResponse
import com.monetizationlib.data.attributes.model.ThemeConfigBuilder
import com.monetizationlib.data.attributes.state.DownloadFeatureUiState
import com.monetizationlib.data.attributes.viewModel.base.StateReducerFlow
import com.monetizationlib.data.attributes.state.DownloadFeatureUiState.Companion.LoadingState
import com.monetizationlib.data.attributes.state.DownloadFeatureUiState.Companion.LoadingType
import com.monetizationlib.data.base.viewModel.BaseViewModel
import com.monetizationlib.data.base.viewModel.ResultAsyncState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

class DownloadAdsViewModel : BaseViewModel<AttributesBussinesModule>() {
    override val businessModule: AttributesBussinesModule
        get() = AttributesBussinesModule

    private val _state = StateReducerFlow(
        initialState = DownloadFeatureUiState.defaultValue,
        reduceState = ::reduceState,
        scope = viewModelScope
    )

    fun getDownloadFeatureState() = _state

    private fun reduceState(
        currentState: DownloadFeatureUiState,
        event: DownloadFeatureEvent,
    ): DownloadFeatureUiState = when (event) {
        is DownloadFeatureEvent.SendOnStepActionComplete -> {
            sendActionComplete(event.currentStep)
            currentState.copy(
                loadingState = Pair(
                    LoadingType.ACTION_COMPLETE, LoadingState.PROCESSING
                )
            )
        }

        is DownloadFeatureEvent.OnStepActionComplete -> {
            currentState.copy(
                loadingState = Pair(
                    LoadingType.ACTION_COMPLETE, LoadingState.COMPLETED
                ),
                config = event.response
            )
        }

        is DownloadFeatureEvent.OnStepActionCompleteFailure -> {
            currentState.copy(
                loadingState = Pair(
                    LoadingType.ACTION_COMPLETE, LoadingState.ERROR
                )
            )
        }

        is DownloadFeatureEvent.OnCurrentStepChange -> {
            currentState.copy(
                loadingState = Pair(
                    LoadingType.ON_STEP_CHANGE, LoadingState.COMPLETED
                ),
                currentStepProgress = event.currentStep,
                config = event.response,
                currentStep = event.currentStepData

            )
        }

        is DownloadFeatureEvent.ResetToDefault -> {
            currentState.copy(
                loadingState = DownloadFeatureUiState.defaultValue.loadingState,
                currentStepProgress = DownloadFeatureUiState.defaultValue.currentStepProgress,
                config = DownloadFeatureUiState.defaultValue.config,
                currentStep = DownloadFeatureUiState.defaultValue.currentStep,
                errorMessage = DownloadFeatureUiState.defaultValue.errorMessage
            )
        }
    }

    private fun sendActionComplete(currentState: StepsResponse) {
        val requestMap: MutableMap<String, Any> = HashMap()
        requestMap["packageName"] = Monetization.packageName
        requestMap["userId"] = Monetization.userId

        viewModelScope.launch {
            when (currentState.type) {
                StepsResponse.StepType.INTRO -> {
                    requestMap["type"] = StepsResponse.StepType.INTRO.remoteName
                }

                StepsResponse.StepType.PLAY_ADS -> {
                    if (currentState.downloadedAppToOpen == null) {
                        _state.handleEvent(DownloadFeatureEvent.OnStepActionCompleteFailure("Error loading downloaded app."))
                        return@launch
                    } else {
                        requestMap["type"] = StepsResponse.StepType.PLAY_ADS.remoteName
                        requestMap["downloadedApp"] = currentState.downloadedAppToOpen!!
                    }

                }

                StepsResponse.StepType.APP_USAGE_TRACKING -> {
                    if ((currentState.timeLeftInSeconds
                            ?: 0.0) <= 0.0 && currentState.isReadyToClaim == true
                    ) {
                        requestMap["type"] = "completeStayInApp"
                    } else {
                        requestMap["type"] = StepsResponse.StepType.APP_USAGE_TRACKING.remoteName
                        requestMap["timeInAppInSeconds"] = currentState.timeSpentInApp
                    }
                }

                StepsResponse.StepType.APP_INSTALL_DURATION -> {
                    requestMap["type"] = StepsResponse.StepType.APP_INSTALL_DURATION.remoteName
                }

                StepsResponse.StepType.RESET_AD_ID -> {
                    requestMap["type"] = StepsResponse.StepType.RESET_AD_ID.remoteName
                    requestMap["advertisementId"] = currentState.adId ?: ""
                }

                else -> {
                    requestMap["type"] = StepsResponse.StepType.FINAL.remoteName
                }
            }
            businessModule.sendDownloadFeatureStepComplete(
                requestMap
            ).asFlow().catch {
                _state.handleEvent(DownloadFeatureEvent.OnStepActionCompleteFailure(it.message.toString()))
            }.collectLatest {
                if (it is ResultAsyncState.Success) {
                    _state.handleEvent(DownloadFeatureEvent.OnStepActionComplete(it.data))
                } else if (it is ResultAsyncState.Failed) {
                    _state.handleEvent(DownloadFeatureEvent.OnStepActionCompleteFailure(it.apiError.userError.toString()))
                }
            }
        }

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getDownloadFeatureConfig(
        appName: String,
        userId: String,
        context: Context,
    ): Flow<DownloadStepConfigResponse?> = callbackFlow {
        val requestMap: MutableMap<String, Any> = HashMap()
        requestMap["packageName"] = appName
        requestMap["userId"] = userId
        businessModule.getDownloadFeatureConfig(requestMap)
            .asFlow()
            .catch {
                trySend(null)
            }.flatMapConcat { result ->
                when (result) {
                    is ResultAsyncState.Success -> {
                        val remoteConfig = result.data
                        if(remoteConfig.steps.isNotEmpty()){
                            remoteConfig.themeConfig = ThemeConfigBuilder
                                .buildWithRemote(context as FragmentActivity, remoteConfig.themeConfig)
                                .getTheme()
                            flowOf(Pair(LoadingState.COMPLETED, remoteConfig))
                        }else{
                            flowOf(Pair(LoadingState.ERROR, null))
                        }
                    }

                    is ResultAsyncState.InProgress, is ResultAsyncState.Started -> {
                        flowOf(Pair(LoadingState.PROCESSING, null))
                    }

                    else -> {
                        flowOf(Pair(LoadingState.ERROR, null))
                    }
                }

            }.collectLatest {
                when (it.first) {
                    LoadingState.ERROR -> {
                        trySend(null)
                    }

                    LoadingState.COMPLETED -> {
                        trySend(it.second)
                    }

                    else -> {

                    }
                }
            }
        awaitClose { this.cancel() }
    }


    fun giveRewardForDownloadedApp(
        packageName: String,
        userId: String,
        isForNewAppDownLoad: Boolean = false,
    ) = businessModule.giveRewardForDownloadedApp(
        packageName,
        isForNewAppDownLoad = isForNewAppDownLoad,
        userId
    )

}