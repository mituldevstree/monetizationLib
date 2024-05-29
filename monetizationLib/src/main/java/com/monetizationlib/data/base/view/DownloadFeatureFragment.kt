package com.monetizationlib.data.base.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.mobilefuse.sdk.utils.dpToPx
import com.monetizationlib.data.R
import com.monetizationlib.data.attributes.model.StepsResponse
import com.monetizationlib.data.attributes.model.ThemeConfig
import com.monetizationlib.data.attributes.state.DownloadFeatureUiState
import com.monetizationlib.data.attributes.viewModel.DownloadAdsViewModel
import com.monetizationlib.data.base.extensions.isPackageInstalled
import com.monetizationlib.data.base.extensions.setAppName
import com.monetizationlib.data.base.view.adapter.AppInstallProgressAdapter
import com.monetizationlib.data.base.view.utility.MonetizationLibBindingAdaptersUtil
import com.monetizationlib.data.base.view.utility.OverlapItemDecoration
import com.monetizationlib.data.databinding.FragmentDownloadStepBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DownloadFeatureFragment : Fragment() {

    lateinit var binding: FragmentDownloadStepBinding
    private var activity: FragmentActivity? = null
    private var downloadStep: StepsResponse? = null
    private var themeConfig: ThemeConfig? = null
    private var maxSteps: Int = 1
    private var currentStep: Int = 0
    private var wasPackageInstalledAndImageLoaded: Boolean = false
    private val requestOptions by lazy {
        RequestOptions()
            .error(R.drawable.icon_download_reward)
            .fallback(R.drawable.icon_download_reward)
            .override(550)
            .dontTransform()
            .dontAnimate().diskCacheStrategy(DiskCacheStrategy.RESOURCE)
    }

    private val mDownloadFeatureViewModel: DownloadAdsViewModel? by lazy {
        activity?.let {
            ViewModelProvider(it)[DownloadAdsViewModel::class.java]
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentDownloadStepBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        setData(downloadStep)
        observerUiState()
    }

    private fun observerUiState() {
        lifecycleScope.launch {
            mDownloadFeatureViewModel?.getDownloadFeatureState()?.collectLatest {
                if (it.loadingState.first == DownloadFeatureUiState.Companion.LoadingType.ON_STEP_CHANGE) {
                    if (it.loadingState.second == DownloadFeatureUiState.Companion.LoadingState.COMPLETED) {
                        currentStep = it.currentStepProgress
                        maxSteps = it.config?.numberOfSteps ?: 1
                        it.currentStep?.let { currentStep ->
                            setData(currentStep)
                        }
                    }
                }
            }
        }
    }

    private fun setData(stepData: StepsResponse?) {
        binding.data = stepData
        binding.themeConfig = themeConfig
        setHeaderImage(binding.data)
        setUpProgressIndicator()
        binding.executePendingBindings()
    }

    override fun onResume() {
        super.onResume()
        if (binding.data?.type == StepsResponse.StepType.APP_USAGE_TRACKING) {
            if (wasPackageInstalledAndImageLoaded.not()) {
                setHeaderImage(binding.data)
            }
        }
    }

    private fun setHeaderImage(currentStepInfo: StepsResponse?) {
        loadDefaultHeader()
        if (currentStepInfo?.type == StepsResponse.StepType.APP_USAGE_TRACKING ||
            currentStepInfo?.type == StepsResponse.StepType.APP_INSTALL_DURATION
        ) {
            if (currentStepInfo.downloadedAppToOpen != null &&
                currentStepInfo.downloadedAppToOpen?.isPackageInstalled(requireActivity()) == true
            ) {
                val packageManager = activity?.packageManager
                packageManager?.let {
                    currentStepInfo.downloadedAppToOpen?.let {
                        it.setAppName(
                            packageManager,
                            binding.txtStepSubTitle,
                            currentStepInfo.subTitle
                        )
                        val appIcon = packageManager.getApplicationIcon(it)
                        wasPackageInstalledAndImageLoaded = true
                        MonetizationLibBindingAdaptersUtil.setImageUrl(
                            binding.imgRooster,
                            appIcon,
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.icon_download_reward
                            ),
                            450
                        )
                        val padding = 35.dpToPx(requireContext())
                        binding.imgRooster.setPadding(padding, padding, padding, padding)
                    }
                }

            }
            val padding = 10.dpToPx(requireContext())
            binding.imgAppOverlay.setPadding(padding, padding, padding, 0)
            binding.imgAppOverlay.visibility = View.VISIBLE
        } else {
            binding.imgAppOverlay.visibility = View.GONE
        }
    }

    private fun loadDefaultHeader() {
        binding.txtStepSubTitle.text = binding.data?.subTitle
        Glide.with(requireContext()).load(binding.data?.image)
            .thumbnail(0.2f)
            .onlyRetrieveFromCache(true)
            .apply(requestOptions)
            .into(binding.imgRooster)
    }

    private fun setUpProgressIndicator() {
        binding.rvSteps.apply {
            addItemDecoration(OverlapItemDecoration(requireContext(), 4, maxSteps))
            this.adapter =
                AppInstallProgressAdapter(currentStep, maxSteps, themeConfig = themeConfig)
        }
    }

    companion object {
        fun newInstance(
            downloadStep: StepsResponse?,
            themeConfig: ThemeConfig?,
            activity: FragmentActivity?,
        ): DownloadFeatureFragment {
            val fragment = DownloadFeatureFragment()
            fragment.downloadStep = downloadStep
            fragment.themeConfig = themeConfig
            fragment.activity = activity
            return fragment
        }
    }

}