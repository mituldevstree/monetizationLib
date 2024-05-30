package com.monetizationlib.data

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.givvymonetization.databinding.ActivityMainBinding
import com.example.givvymonetization.databinding.LayoutAdsContainerBinding
import com.monetizationlib.data.Monetization.isDownloadFeatureReadyToClaimNextStep
import com.monetizationlib.data.application.Controller.Companion.wasMonetizationInitialized
import com.monetizationlib.data.base.model.LocaleManager
import com.monetizationlib.data.base.model.entities.RewardLimitationDataConfig
import com.monetizationlib.data.base.view.utility.StepProcessingError
import com.monetizationlib.data.localcache.LocalDataHelper
import com.monetizationlib.data.viewmodel.UserViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.util.Locale


class MainActivity : AppCompatActivity(), Observer {

    private lateinit var mBinding: ActivityMainBinding
    val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        if (!isTaskRoot) {
            finish()
            return
        }
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        mBinding.btnBonusFlow.isEnabled = false
        mBinding.loadingView.visibility = View.VISIBLE

        initMonetizationLib()
//        if (wasMonetizationInitialized.not()) {
//            userViewModel.versionCheck(onError = {
//                MainScope().launch {
//                    mBinding.loadingView.visibility = View.GONE
//                    Toast.makeText(this@MainActivity, "Failed to fetch user", Toast.LENGTH_SHORT)
//                        .show()
//                }
//            }, onProgressComplete = {
//                MainScope().launch {
//                    setUserInfo()
//                    initMonetizationLib()
//                }
//
//            })
//        }


        /*  val settings = AppLovinSdkSettings(this)
            settings.testDeviceAdvertisingIds = listOf("8f23cc9a-40b3-4r06-8be5-032200c006c5")
            settings.isMuted = true
            AppLovinSdk.getInstance(settings, this).showMediationDebugger()*/


        mBinding.btnAds.setOnClickListener {
            Monetization.showBestAd()
        }

        mBinding.btnBonusFlow.setOnClickListener {
            openDownloadFeature()
        }
    }


    private fun openDownloadFeature() {
        Monetization.openDownloadFeature(
            mBinding.loadingView,
            onRewardClaimed = { earningInfo ->
                Toast.makeText(
                    this@MainActivity,
                    "Rewarded:${earningInfo?.earnedCredits?.div(10000)} USD",
                    Toast.LENGTH_SHORT
                ).show()
                val user = LocalDataHelper.getUserDetail().also {
                    it?._userBalance = (earningInfo?.currentUserBalance ?: 0.0).toFloat()
                }
                LocalDataHelper.setUserDetail(user)
                setUserInfo()
            },
            onStepProcessingFailure = { errorState ->
                when (errorState) {
                    StepProcessingError.APP_NOT_INSTALLED -> {
                        Toast.makeText(
                            this@MainActivity,
                            errorState.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    StepProcessingError.AD_NOT_AVAILABLE -> {
                        Toast.makeText(
                            this@MainActivity,
                            "Currently Ads are not available",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    StepProcessingError.API_ERROR -> {
                        Toast.makeText(
                            this@MainActivity,
                            errorState.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    StepProcessingError.AD_LIMIT_REACHED -> {
                        Toast.makeText(
                            this@MainActivity,
                            "Daily earning limit reached in this app",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            },
            onUserCancel = {
                Toast.makeText(this@MainActivity, "Flow closed by user", Toast.LENGTH_SHORT)
                    .show()
            })
    }

    private fun setUserInfo() {
        val stringBundle = StringBuilder()
        stringBundle.append("UserName: ${LocalDataHelper.getUserDetail()?._username}")
            .append("\n")
            .append("UserID: ${LocalDataHelper.getUserDetail()?.id}")
            .append("\n")
            .append("UserBalance: ${LocalDataHelper.getUserDetail()?._userBalance} ")
        mBinding.txtUserInfo.text = stringBundle.toString()
    }

    override fun attachBaseContext(base: Context?) {
        val locale = Locale("es")
        Locale.setDefault(locale)
        val configuration = base?.resources?.configuration
        configuration?.setLocale(locale)
        val newBaseContext = configuration?.let { base.createConfigurationContext(it) }
        newBaseContext?.let { super.attachBaseContext(newBaseContext) }
    }

    override fun onResume() {
        super.onResume()
        Monetization.onResume(this)
    }


    override fun onDownloadFeatureAvailable() {
        super.onDownloadFeatureAvailable()
        mBinding.btnBonusFlow.visibility = View.VISIBLE
        //autoOpenDownloadFlow()
    }

    override fun onDestroy() {
        super.onDestroy()
        wasMonetizationInitialized = false
        Monetization.onDestroy()
    }

    override fun onAdLimitReached(rewardLimitationDataConfig: RewardLimitationDataConfig) {

    }

    override fun onAppLovinInitializedSuccessfully() {
        super.onAppLovinInitializedSuccessfully()
    }

    private fun initMonetizationLib() {
        LocaleManager.getCurrentLanguageBackendHeaderName(this).let {
            Monetization.startInitialization(
                lang = "Spanish",
                userId = LocalDataHelper.getUserDetail()?.id ?: "654b57f46972ec0002d89776",
                email = LocalDataHelper.getUserDetail()?._email ?: "",
                packageName = "com.treasure.hunter",
                versionName = "1.5",
                activity = this,
                isXpApp = false,
                showConsent = { needToShowConsent ->
                    if (needToShowConsent) {
                        Monetization.showConsentFlow(this@MainActivity) {
                            onMonetizationFinalize()
                        }
                    } else {
                        onMonetizationFinalize()
                    }
                },
                shouldPrintLogs = true,
                shouldMuteAds = true
            )
        }
    }

    private fun onMonetizationFinalize() {
        Monetization.addMonetizationObserver(this)
//        AppLovinSdk.getInstance(this).showMediationDebugger()
        MainScope().launch {
            mBinding.btnBonusFlow.isEnabled = true
            mBinding.loadingView.visibility = View.GONE
            wasMonetizationInitialized = true
            //autoOpenDownloadFlow()
            Monetization.checkIfDownloadFeatureIsAvailable()
        }

    }

    private fun autoOpenDownloadFlow() {
        isDownloadFeatureReadyToClaimNextStep(
            mBinding.loadingView,
            callback = { isReadyForClaim, _ ->
                if (isReadyForClaim) {
                    openDownloadFeature()
                }
            },
            autoOpenFlow = false,
        )
    }



    override fun onFairBidBannerLoad() {
        super.onFairBidBannerLoad()
    }

    override fun onFairBidBannerFailedToLoad() {
        super.onFairBidBannerFailedToLoad()
        Log.e("Banner", "Failed")
    }

}