package com.monetizationlib.data.base.extensions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.monetizationlib.data.Monetization
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@ColorInt
fun Context.getAppColor(@AttrRes colorAttr: Int): Int {
    val resolvedAttr = resolveThemeAttr(this, colorAttr)
    return if (resolvedAttr.resourceId != 0) {
        return ContextCompat.getColor(this, resolvedAttr.resourceId)
    } else {
        resolvedAttr.data
    }
}

@ColorRes
fun Context.getAppColorRes(@AttrRes colorAttr: Int): Int {
    val resolvedAttr = resolveThemeAttr(this, colorAttr)
    // resourceId is used if it's a ColorStateList, and data if it's a color reference or a hex color
    return if (resolvedAttr.resourceId != 0) resolvedAttr.resourceId else 0
}

private fun resolveThemeAttr(context: Context, @AttrRes attrRes: Int): TypedValue {
    val typedValue = TypedValue()
    context.theme.resolveAttribute(attrRes, typedValue, true)
    return typedValue
}


fun Activity.resetAdIdIntent() {
    val resetIdAction = "com.google.android.gms.settings.ADS_PRIVACY"
    val settingsIntent = Intent(resetIdAction)
    settingsIntent.flags =
        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
    this.startActivity(settingsIntent)
}


suspend fun Context.getAdvertisingId(): String =
    withContext(Dispatchers.IO) {
        val adInfo = AdvertisingIdClient(this@getAdvertisingId)
        //Connect with start(), disconnect with finish()
        adInfo.start()
        val adIdInfo = adInfo.info
        adIdInfo.id ?: ""
    }


fun Context.insertInPreference(key: String, value: String) {
    this.getSharedPreferences(
        Monetization.packageName, Context.MODE_PRIVATE
    )?.edit()?.putString(key, value)
        ?.apply()
}

fun Context.getFromPreference(key: String): String? {
    return this.getSharedPreferences(Monetization.packageName, Context.MODE_PRIVATE)
        ?.getString(key, "")
}