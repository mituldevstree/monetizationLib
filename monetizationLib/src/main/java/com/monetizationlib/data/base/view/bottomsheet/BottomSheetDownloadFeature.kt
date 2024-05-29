package com.monetizationlib.data.base.view.bottomsheet



import android.graphics.Color
import android.graphics.drawable.GradientDrawable.Orientation
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.monetizationlib.data.R
import com.monetizationlib.data.attributes.event.DownloadFeatureEvent
import com.monetizationlib.data.attributes.model.DownloadStepConfigResponse
import com.monetizationlib.data.attributes.model.StepsResponse
import com.monetizationlib.data.attributes.state.DownloadFeatureUiState
import com.monetizationlib.data.attributes.viewModel.DownloadAdsViewModel
import com.monetizationlib.data.base.extensions.getAdvertisingId
import com.monetizationlib.data.base.extensions.insertInPreference
import com.monetizationlib.data.base.extensions.isPackageInstalled
import com.monetizationlib.data.base.view.DownloadFeatureFragment
import com.monetizationlib.data.base.view.adapter.ViewPagerAdapter
import com.monetizationlib.data.base.view.utility.AppUsageStatsManager
import com.monetizationlib.data.base.view.utility.MonetizationLibBindingAdaptersUtil
import com.monetizationlib.data.base.view.utility.StepProcessingError
import com.monetizationlib.data.base.view.utility.TimerUtils
import com.monetizationlib.data.databinding.BottomsheetDownloadIntroBinding
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Locale

internal class BottomSheetDownloadFeature : MonetizationBaseBottomSheet(), View.OnClickListener {

    lateinit var binding: BottomsheetDownloadIntroBinding
    var callback: IDialogButtonClick? = null
    private var activity: FragmentActivity? = null
    private var downloadFeatureConfig: DownloadStepConfigResponse? = null
    private var viewPagerAdapter: ViewPagerAdapter? = null
    private var currentStep: StepsResponse? = null
    private var installTimer: TimerUtils? = null
    private var wasOpened: Boolean = false

    private val mDownloadFeatureViewModel: DownloadAdsViewModel? by lazy {
        activity?.let {
            ViewModelProvider(it)[DownloadAdsViewModel::class.java]
        }
    }

    private val appUsageManager: AppUsageStatsManager by lazy {
        AppUsageStatsManager(
            requireActivity()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = BottomsheetDownloadIntroBinding.inflate(inflater, container, false)
        binding.themeConfig = downloadFeatureConfig?.themeConfig
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (dialog is BottomSheetDialog) {
            val behaviour = (dialog as BottomSheetDialog).behavior
            behaviour.state = BottomSheetBehavior.STATE_EXPANDED
            behaviour.isDraggable = false
            behaviour.isHideable = false
            dialog?.window?.setBackgroundDrawable(
                MonetizationLibBindingAdaptersUtil.GradeDrawable(
                    Orientation.TOP_BOTTOM,
                    Color.parseColor(
                        downloadFeatureConfig?.themeConfig?.mainComponent?.windowBackground
                            ?: "#1F2336"
                    ),
                    Color.parseColor(
                        downloadFeatureConfig?.themeConfig?.mainComponent?.windowBackground
                            ?: "#1F2336"
                    ),
                    0f, 0f, 0f, 0f
                )
            )
        }
        initUi()
    }

    private fun initUi() {
        setExpanded(true)
        binding.clickListener = this
        observeApiState()
        setupViewPagerAdapter()
    }

    private fun observeApiState() {
        lifecycleScope.launch {
            mDownloadFeatureViewModel?.getDownloadFeatureState()?.collectLatest {
                if (it.loadingState.first == DownloadFeatureUiState.Companion.LoadingType.ACTION_COMPLETE) {
                    when (it.loadingState.second) {
                        DownloadFeatureUiState.Companion.LoadingState.COMPLETED -> {
                            toggleLoadingState(hasProcessingFinished = true)
                            downloadFeatureConfig?.updateCurrentState(it.config)
                            currentStep = it.config?.steps?.find { item ->
                                item.step == currentStep?.step
                            }
                            if (currentStep != null) {
                                when (currentStep!!.type) {
                                    StepsResponse.StepType.INTRO -> {
                                        goToNextStep()
                                    }

                                    StepsResponse.StepType.PLAY_ADS -> {
                                        goToNextStep()
                                    }

                                    StepsResponse.StepType.APP_USAGE_TRACKING -> {
                                        if (currentStep?.isCompleted == true)
                                            goToNextStep()
                                        else
                                            manageButtonState()
                                    }

                                    StepsResponse.StepType.APP_INSTALL_DURATION -> {
                                        manageButtonState()
                                    }

                                    StepsResponse.StepType.RESET_AD_ID -> {
                                        if (currentStep?.isCompleted == true)
                                            goToNextStep()
                                        else
                                            manageButtonState()
                                    }

                                    StepsResponse.StepType.FINAL -> {
                                        if (downloadFeatureConfig?.isRewardClaimed == true) {
                                            callback?.onFlowCompletion(downloadFeatureConfig)
                                            mDownloadFeatureViewModel?.getDownloadFeatureState()
                                                ?.handleEvent(
                                                    DownloadFeatureEvent.ResetToDefault(
                                                        downloadFeatureConfig
                                                    )
                                                )
                                        } else {
                                            callback?.onStepProcessingError(StepProcessingError.API_ERROR.also { value ->
                                                value.message =
                                                    getString(R.string.failed_to_claim_reward_please_close_and_retry_else_check_your_app_balance_after_restarting_your_app)
                                            })
                                        }

                                    }

                                    else -> {
                                        callback?.onStepProcessingError(StepProcessingError.API_ERROR.also { value ->
                                            value.message =
                                                getString(R.string.unknown_step_found)
                                        })
                                    }
                                }

                            }
                        }

                        DownloadFeatureUiState.Companion.LoadingState.PROCESSING -> {
                            if (currentStep?.type != StepsResponse.StepType.APP_INSTALL_DURATION || installTimer?.isTimerRunning()
                                    ?.not() == true
                            )
                                toggleLoadingState(hasProcessingFinished = false)
                        }

                        else -> {
                            callback?.onStepProcessingError(StepProcessingError.API_ERROR)
                            toggleLoadingState(hasProcessingFinished = true)
                        }
                    }

                }

            }
        }
    }

    private fun toggleLoadingState(hasProcessingFinished: Boolean) {
        binding.btnNext.isEnabled = hasProcessingFinished
        this@BottomSheetDownloadFeature.isCancelable = hasProcessingFinished
        binding.btnClose.isEnabled = hasProcessingFinished
        binding.loadingIndicator.visibility = if (hasProcessingFinished) GONE else VISIBLE
    }

    private fun setupViewPagerAdapter() {

        viewPagerAdapter = ViewPagerAdapter(childFragmentManager, lifecycle, getFragmentList())
        binding.viewPager.apply {
            adapter = viewPagerAdapter
            isUserInputEnabled = false
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    currentStep = downloadFeatureConfig?.steps?.get(position)
                    mDownloadFeatureViewModel?.getDownloadFeatureState()?.handleEvent(
                        DownloadFeatureEvent.OnCurrentStepChange(
                            downloadFeatureConfig?.currentStep ?: 0,
                            downloadFeatureConfig,
                            currentStep
                        )
                    )
                    manageButtonState()
                }
            })
        }
        lifecycleScope.launch {
            delay(100)
            binding.viewPager.setCurrentItem(downloadFeatureConfig?.currentStep ?: 0, false)
        }

    }

    override fun onResume() {
        super.onResume()
        checkAppStepStates()

    }

    override fun onPause() {
        super.onPause()
    }

    private fun checkAppStepStates() {
        MainScope().launch {
            delay(800)
            when (currentStep?.type) {
                StepsResponse.StepType.APP_USAGE_TRACKING -> {
                    appUsageManager.setPackageToCheck(currentStep?.downloadedAppToOpen)
                        .checkAndReturnTimeSpent(shouldShowPermissionRequest = false) { appInForegroundTime, errorMessage ->
                            if (appInForegroundTime != null) {
                                currentStep?.timeSpentInApp = appInForegroundTime
                                if (currentStep?.isReadyToClaim == false && (currentStep?.getRemainingAppUsageTime()
                                        ?: 0) == 0L
                                ) {
                                    communicateActionCompleteToBackend()
                                } else {
                                    manageButtonState()
                                }
                            } else {
                                currentStep?.timeSpentInApp = 0
                                callback?.onStepProcessingError(StepProcessingError.APP_NOT_INSTALLED.also {
                                    it.message = errorMessage ?: ""
                                })
                            }
                        }
                }

                StepsResponse.StepType.APP_INSTALL_DURATION -> {
                    manageButtonState()
                    if (currentStep?.isReadyToClaim == false
                        && (currentStep?.timeLeftInSeconds ?: 0.0) > 0.0
                        && currentStep?.isCompleted == false
                    ) {
                        installTimer?.pauseTimer()
                        communicateActionCompleteToBackend() // Call to get updated timer.
                    }

                }

                StepsResponse.StepType.RESET_AD_ID -> {
                    val adID = context?.getAdvertisingId()
                    if (currentStep?.adId == null) {
                        currentStep?.adId = adID
                    } else if (currentStep?.adId?.equals(adID)?.not() == true && wasOpened) {
                        adID?.let {
                            context?.insertInPreference("adId", adID)
                        }
                        communicateActionCompleteToBackend() // Call to get updated timer.
                    }
                }

                else -> {
                    manageButtonState()
                }
            }
        }
    }

    private fun manageButtonState() {
        when (currentStep?.type) {
            StepsResponse.StepType.INTRO -> {
                binding.btnNext.text = currentStep?.buttonText
            }

            StepsResponse.StepType.PLAY_ADS -> {
                binding.btnNext.text = currentStep?.buttonText
                binding.btnNext.isEnabled = true
            }

            StepsResponse.StepType.APP_USAGE_TRACKING -> {
                appUsageManager.setPackageToCheck(currentStep?.downloadedAppToOpen)
                    .checkAndReturnTimeSpent(shouldShowPermissionRequest = true) { appInForegroundTime, _ ->
                        if (appInForegroundTime != null) {
                            currentStep?.timeSpentInApp = appInForegroundTime
                        } else {
                            currentStep?.timeSpentInApp = 0
                            callback?.onStepProcessingError(StepProcessingError.APP_NOT_INSTALLED)
                        }
                    }

                when {
                    currentStep?.getRemainingAppUsageTime() == currentStep?.timeLeftInSeconds?.toLong() -> {
                        binding.btnNext.isSelected = false
                        binding.btnNext.isEnabled = true
                        binding.btnNext.text = getString(R.string.open_app)
                    }

                    currentStep?.hasTimer == true &&
                            (currentStep?.timeLeftInSeconds ?: 0.0) > 0.0 &&
                            currentStep?.getRemainingAppUsageTime() != 0L -> {
                        binding.btnNext.isSelected = true
                        binding.btnNext.isEnabled = true
                        binding.btnNext.text = currentStep?.getFormattedRemainingTime()
                    }

                    (currentStep?.timeLeftInSeconds
                        ?: 0.0) <= 0.0 && currentStep?.isReadyToClaim == true -> {
                        binding.btnNext.isSelected = false
                        binding.btnNext.isEnabled = true
                        binding.btnNext.text = getString(R.string.claim)
                    }
                }
            }

            StepsResponse.StepType.APP_INSTALL_DURATION -> {
                if (currentStep?.isReadyToClaim == false
                    && (currentStep?.timeLeftInSeconds ?: 0.0) > 0.0
                    && currentStep?.isCompleted == false
                ) {
                    if (currentStep?.downloadedAppToOpen?.isPackageInstalled(context = requireActivity()) == true) {
                        initInstallTimer((currentStep?.timeLeftInSeconds ?: 0.0).toLong())
                        binding.btnNext.isSelected = true
                        binding.btnNext.isEnabled = true
                    } else {
                        callback?.onStepProcessingError(StepProcessingError.APP_NOT_INSTALLED)
                        binding.btnNext.isSelected = true
                        binding.btnNext.isEnabled = true
                        binding.btnNext.text = getString(R.string.download_app)
                    }
                } else {
                    if (installTimer != null) {
                        installTimer?.destroyTimer()
                        installTimer = null
                    }
                    binding.btnNext.isSelected = false
                    binding.btnNext.isEnabled = true
                    binding.btnNext.text = currentStep?.buttonText
                }
            }

            StepsResponse.StepType.RESET_AD_ID -> {
                MainScope().launch {
                    val adID = context?.getAdvertisingId()
                    if (currentStep?.adId == null) {
                        currentStep?.adId = adID
                    }
                    binding.btnNext.text = currentStep?.buttonText
                }
            }

            else -> {
                binding.btnNext.text = currentStep?.buttonText
            }
        }

    }

    private fun getFragmentList(): ArrayList<Fragment> {
        val list = ArrayList<Fragment>()
        downloadFeatureConfig?.steps?.forEach { stepResponse ->
            list.add(
                DownloadFeatureFragment.newInstance(
                    activity = activity,
                    downloadStep = stepResponse,
                    themeConfig = downloadFeatureConfig?.themeConfig
                )
            )
        }
        return list
    }

    override fun onClick(view: View?) {
        when (view) {
            binding.btnClose -> {
                callback?.onUserCancel()
                dismissAllowingStateLoss()
            }

            binding.btnNext -> {
                if (checkIfUserCanContinueToNextStep()) {
                    communicateActionCompleteToBackend()
                } else {
                    when (currentStep?.type) {
                        StepsResponse.StepType.APP_USAGE_TRACKING -> {
                            appUsageManager.setPackageToCheck(currentStep?.downloadedAppToOpen)
                                .checkAndShowPermission(shouldShowPermissionRequest = true) {
                                    if (currentStep?.downloadedAppToOpen?.isPackageInstalled(
                                            requireActivity()
                                        ) == true
                                    ) {
                                        currentStep?.hasOpenedDownloadedApp = true
                                    }
                                    callback?.onNextStep(currentStep)
                                }
                        }

                        StepsResponse.StepType.RESET_AD_ID -> {
                            wasOpened = true
                            callback?.onNextStep(currentStep)
                        }

                        else -> {
                            callback?.onNextStep(currentStep)
                        }
                    }

                }
            }
        }
    }


    override fun dismiss() {
        super.dismiss()
        callback?.onDismiss()
    }

    private fun communicateActionCompleteToBackend() {
        currentStep?.let {
            mDownloadFeatureViewModel?.getDownloadFeatureState()
                ?.handleEvent(DownloadFeatureEvent.SendOnStepActionComplete(it))
        }
    }

    private fun checkIfUserCanContinueToNextStep(): Boolean {
        return if (currentStep != null) {
            when (currentStep?.type) {
                StepsResponse.StepType.INTRO -> {
                    true
                }

                StepsResponse.StepType.PLAY_ADS -> {
                    // check app install callback.
                    binding.btnNext.isEnabled = false
                    false
                }

                StepsResponse.StepType.APP_USAGE_TRACKING -> {
                    // check app spent time.
                    if ((currentStep?.timeLeftInSeconds ?: 0.0) <= 0.0) {
                        true
                    } else if (currentStep?.hasOpenedDownloadedApp == false) {
                        false
                    } else
                        false
                }

                StepsResponse.StepType.APP_INSTALL_DURATION -> {
                    if (currentStep?.isReadyToClaim == true && currentStep?.isCompleted == false) {
                        return true
                    } else {
                        false
                    }
                }

                StepsResponse.StepType.RESET_AD_ID -> {
                    false
                }

                StepsResponse.StepType.FINAL -> {
                    true
                }

                else -> {
                    false
                }
            }
        } else {
            false

        }
    }


    companion object {
        val TAG = BottomSheetDownloadFeature::class.java.simpleName
        fun newInstance(
            activity: FragmentActivity?,
            actionCallback: IDialogButtonClick,
        ): BottomSheetDownloadFeature {
            val fragment = BottomSheetDownloadFeature()
            fragment.activity = activity
            fragment.callback = actionCallback
            return fragment
        }
    }

    fun setDownloadConfigDetails(downloadFeatureConfig: DownloadStepConfigResponse): BottomSheetDownloadFeature {
        this.downloadFeatureConfig = downloadFeatureConfig
        return this
    }

    fun goToNextStep() {
        binding.viewPager.let { pager ->
            if (pager.currentItem < (viewPagerAdapter?.itemCount ?: 0).minus(1)) {
                pager.setCurrentItem(pager.currentItem.plus(1), true)
            }
        }
    }

    fun onAppDownloadCompleted(packageNameToOpen: String?) {
        MainScope().launch {
            delay(500)
            binding.btnNext.isEnabled = true
            if (packageNameToOpen.isNullOrEmpty().not()) {
                if (packageNameToOpen.equals(currentStep?.downloadedAppToOpen, ignoreCase = true)
                        .not()
                ) {
                    if (currentStep?.type == StepsResponse.StepType.PLAY_ADS) {
                        currentStep?.downloadedAppToOpen = packageNameToOpen
                        communicateActionCompleteToBackend()
                    }
                }
            } else {
                // display message to replay ads.
                callback?.onStepProcessingError(StepProcessingError.APP_NOT_INSTALLED)
            }

        }

    }

    fun resetButtonState() {
        binding.btnNext.isSelected = false
        binding.btnNext.isEnabled = true
        manageButtonState()
    }

    interface IDialogButtonClick {
        fun onNextStep(data: StepsResponse?)
        fun onUserCancel()
        fun onDismiss()

        fun onFlowCompletion(downloadFeatureConfig: DownloadStepConfigResponse?)

        fun onStepProcessingError(errorMessage: StepProcessingError)
    }


    private fun initInstallTimer(remainingSeconds: Long) {
        if (installTimer == null) {
            installTimer = TimerUtils(this@BottomSheetDownloadFeature.lifecycle).setMillisInFuture(
                remainingSeconds.times(1000)
            ).setCountDownInterval(1000).setTimerUpdateListener(object : TimerUtils.TimerListeners {
                override fun onTimerUpdate(hours: Long, minutes: Long, seconds: Long) {
                    binding.btnNext.text = String.format(
                        Locale.ENGLISH, "%02d:%02d:%02d", hours, minutes, seconds
                    )
                    if (seconds.toInt() % 15 == 0) {
                        communicateActionCompleteToBackend()
                    }

                }

                override fun onTimerFinish() {
                    if (currentStep?.isReadyToClaim == false)
                        communicateActionCompleteToBackend()
                    else
                        manageButtonState()
                }
            }).build()
            installTimer?.startTimer()
        } else {
            installTimer?.updateTime(remainingSeconds.times(1000))
        }

    }
}