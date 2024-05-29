package com.monetizationlib.data.base.extensions

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.text.Html
import android.text.SpannableString
import android.text.Spanned
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.browser.customtabs.CustomTabsIntent
import com.google.android.exoplayer2.util.Log
import com.google.gson.GsonBuilder
import com.monetizationlib.data.R

fun String.openWebPage(context: Context?) {
    this.validateUrl(context, completionBlock = { webPage, intent ->
        context?.startActivity(intent)
    })
}


fun String.openCustomTab(context: Context?) {
    this.validateUrl(context, completionBlock = { webPage, intent ->
        // Use a CustomTabsIntent.Builder to configure CustomTabsIntent.
        // Once ready, call CustomTabsIntent.Builder.build() to create a CustomTabsIntent
        // and launch the desired Url with CustomTabsIntent.launchUrl()
        val builder = CustomTabsIntent.Builder()
        context?.let {
            builder.setToolbarColor(it.resources.getColor(R.color.colorPurple))
        }
        val customTabsIntent = builder.build()
        context?.let { contextWrapped ->
            if (intent.resolveActivity(contextWrapped.packageManager) != null) {
                customTabsIntent.launchUrl(contextWrapped, webPage)
            }
        }
    })
}


fun String.validateUrl(context: Context?, completionBlock: (webPage: Uri, intent: Intent) -> Unit) {

    var webPage = Uri.parse(this)

    if (!this.startsWith("http://") && !this.startsWith("https://")) {
        webPage = Uri.parse("https://$this")
    }

    val intent = Intent(Intent.ACTION_VIEW, webPage)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    if (context?.packageManager?.let { intent.resolveActivity(it) } != null) {
        completionBlock(webPage, intent)
    }
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}


/*
* Execute block into try...catch
* */
inline fun <T> justTry(tryBlock: () -> T) = try {
    tryBlock()
} catch (e: Exception) {
    e.printStackTrace()
}


val gson = GsonBuilder().disableHtmlEscaping().create()
fun Any?.toJson(): String = gson.toJson(this)


@SuppressWarnings("deprecation")
fun String?.fromHtml(): Spanned {
    if (this == null)
        return SpannableString("")
    else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        // FROM_HTML_MODE_LEGACY is the behaviour that was used for versions below android N
        // we are using this flag to give a consistent behaviour
        return Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
    } else {
        return Html.fromHtml(this)
    }
}


fun String?.openAppByPackageName(context: Activity) {
    this?.let {
        if (this.isPackageInstalled(context)) {
            val startIntent = context.packageManager.getLaunchIntentForPackage(this)
            if (startIntent != null) {
                context.startActivity(startIntent)
            }
        } else {
            this.redirectToPlaystore(context)
        }

    }
}

fun String.isPackageInstalled(context: Activity): Boolean {
    return if (this.isEmpty()) {
        false
    } else context.packageManager.getInstalledApplications(0)
        .find { info -> info.packageName == this } != null
}

fun String.redirectToPlaystore(context: Activity) {
    try {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$this")))
    } catch (anfe: ActivityNotFoundException) {
        context.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=$this")
            )
        )
    }
}

@Suppress("DEPRECATION")
fun String.getPackageInfo(packageManager: PackageManager?): PackageInfo? {
    Log.e("PackageName", this)
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        packageManager?.getPackageInfo(this, PackageManager.PackageInfoFlags.of(0))
    } else {
        packageManager?.getPackageInfo(this, 0)
    }
}

@Suppress("DEPRECATION")
fun String.setAppName(packageManager: PackageManager?, textView: TextView, prefixString:String?) {
    Log.e("PackageName", this)
    val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        packageManager?.getApplicationInfo(this, PackageManager.ApplicationInfoFlags.of(0))
    } else {
        packageManager?.getApplicationInfo(this, 0)
    }

    if (packageInfo != null) {
        val appName = packageManager?.getApplicationLabel(packageInfo)
        textView.text = prefixString.plus("\n").plus(appName)
    }
}


fun String.isValidColor(): Boolean {
    return this.startsWith("#") && (this.trim().length == 4 || this.trim().length == 7 || this.trim().length == 9)
}

