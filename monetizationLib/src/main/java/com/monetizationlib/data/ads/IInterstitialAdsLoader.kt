package com.monetizationlib.data.ads

import android.content.Context
import androidx.annotation.MainThread
import com.monetizationlib.data.ads.model.GivvyAd

interface IInterstitialAdsLoader {
    fun initialize(context: Context)
    @MainThread
    fun showAd(): GivvyAd
    fun loadAd()
    fun adDismissed()
    fun onAdLoadedButNotReady()
}
