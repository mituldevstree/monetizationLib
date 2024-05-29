package com.monetizationlib.data.ads

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

interface INativeAdsLoader {
    fun loadNewAd(
        inflater: LayoutInflater,
        adLayout: ViewGroup,
        adAdditionalKey: String,
        existingView: View?,
        designUnitType: DesignUnitType,
        adSignature: String?,
        withLoader: Boolean
    ): View?

    fun setContext(context: Context)
    fun setActivity(activity: Activity)
    fun setForeground(isForeground: Boolean)
    fun startInit()
    fun loadNewAds()
    fun onImpressionMade()
    fun onDestroy()
}
