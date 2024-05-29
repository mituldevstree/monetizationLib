package com.monetizationlib.data.base.view.utility

import android.app.AppOpsManager
import android.app.Dialog
import android.app.usage.UsageEvents.Event
import android.app.usage.UsageEvents.Event.ACTIVITY_PAUSED
import android.app.usage.UsageEvents.Event.ACTIVITY_RESUMED
import android.app.usage.UsageEvents.Event.ACTIVITY_STOPPED
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Process
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.monetizationlib.data.Monetization
import com.monetizationlib.data.R
import com.monetizationlib.data.base.extensions.getPackageInfo
import com.monetizationlib.data.base.extensions.getSecondsDiffBetweenCurrentDate
import com.monetizationlib.data.base.extensions.getSecondsDiffBetweenDate
import com.monetizationlib.data.base.extensions.isPackageInstalled
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class AppUsageStatsManager(val context: FragmentActivity) {

    private var appOpsManager: AppOpsManager? = null
    private var packageManager: PackageManager? = null
    private var permissionDialog: Dialog? = null
    private var packageToCheck: String? = null

    init {
        appOpsManager = context.getSystemService(AppCompatActivity.APP_OPS_SERVICE) as AppOpsManager
        packageManager = context.packageManager
    }

    fun setPackageToCheck(packageName: String?): AppUsageStatsManager {
        this.packageToCheck = packageName
        return this
    }

    fun checkAndReturnTimeSpent(
        shouldShowPermissionRequest: Boolean,
        result: (appInForegroundTime: Long?, errorMessage: String?) -> Unit,
    ) {
        if (packageToCheck == null) result.invoke(null, "Package not found")
        else
            checkAndShowPermission(shouldShowPermissionRequest) {
                queryForAppUsage { spentTime, errorMessage ->
                    result.invoke(spentTime, errorMessage)
                }
            }
    }

    fun checkAndShowPermission(
        shouldShowPermissionRequest: Boolean,
        canQueryRequest: () -> Unit,
    ) {
        if (checkUsageStatsPermission().not()) {
            if (shouldShowPermissionRequest) {
                shouldRequestDialog {
                    val intent = Intent(
                        Settings.ACTION_USAGE_ACCESS_SETTINGS,
                        Uri.parse("package:" + Monetization.packageName)
                    )
                    context.startActivity(intent)
                }

            }
        } else {
            canQueryRequest.invoke()
        }
    }

    private fun shouldRequestDialog(callback: () -> Unit) {
        MainScope().launch {
            delay(400)
            if (permissionDialog == null)
                permissionDialog = MaterialAlertDialogBuilder(context)
                    .setIcon(R.drawable.dialog_header_permission)
                    .setTitle(R.string.app_needs_usage_permission)
                    .setCancelable(false)
                    .setMessage(context.getString(R.string.in_order_to_complete_and_claim_this_step_app_needs_permission_to_access_app_usage_stats_to_check_time_spent_in_the_app))
                    .setPositiveButton(
                        context.getString(R.string.grant)
                    ) { _, _ ->
                        permissionDialog = null
                        callback.invoke()
                    }
                    .show()
        }

    }


    private fun queryForAppUsage(resultCallback: (spentTime: Long?, errorMessage: String?) -> Unit) {
        if (packageToCheck.isNullOrEmpty().not()) {
            if (packageToCheck?.isPackageInstalled(context) == true) {
                val allEvents: MutableList<Event> = ArrayList()
                val endRangeTime = System.currentTimeMillis()
                val startRangeTime = endRangeTime.minus(1000 * 60 * 15)
                val usageStatsManager =
                    context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
                val usageEvents = usageStatsManager.queryEvents(startRangeTime, endRangeTime)

                while (usageEvents.hasNextEvent()) {
                    val myPackageEvent = Event()
                    usageEvents.getNextEvent(myPackageEvent)
                    if (myPackageEvent.packageName.contains(packageToCheck!!)) {
                        if (myPackageEvent.eventType == ACTIVITY_RESUMED
                            || myPackageEvent.eventType == ACTIVITY_PAUSED
                            || myPackageEvent.eventType == ACTIVITY_STOPPED
                        ) {
                            allEvents.add(myPackageEvent)
                        }
                    }
                }

                /*  val appInfo = usageStatsManager.queryUsageStats(
                      UsageStatsManager.INTERVAL_BEST, startRangeTime,
                      endRangeTime
                  ).find { it.packageName == packageToCheck }*/


                if (allEvents.isEmpty().not()) {

                  /*  val startTime = allEvents.first { it.eventType == ACTIVITY_RESUMED }
                    val endTime =
                        allEvents.last { it.eventType == ACTIVITY_PAUSED || it.eventType == ACTIVITY_STOPPED }

                    val diff = endTime.timeStamp.getSecondsDiffBetweenDate(startTime.timeStamp)
                    val spentTime = diff*/

                    var spentTime = 0L
                    var eventStartTime = 0L
                    var eventEndTime = 0L
                    allEvents.forEachIndexed { index, it ->
                        when (it.eventType) {
                            ACTIVITY_RESUMED -> {
                                if (eventStartTime == 0L) {
                                    eventStartTime = it.timeStamp
                                } else if (eventEndTime != 0L) {
                                    val diff = eventEndTime.getSecondsDiffBetweenDate(eventStartTime)
                                    spentTime = spentTime.plus(diff)
                                    eventStartTime = it.timeStamp
                                    eventEndTime = 0L
                                }
                            }
                            ACTIVITY_PAUSED, ACTIVITY_STOPPED -> {
                                if (eventStartTime != 0L && eventEndTime == 0L) {
                                    eventEndTime = it.timeStamp
                                } else if (eventStartTime != 0L && eventEndTime != 0L) {
                                    eventEndTime = it.timeStamp
                                    if (index == allEvents.size.minus(1)) {
                                        val diff =
                                            eventEndTime.getSecondsDiffBetweenDate(eventStartTime)
                                        spentTime = spentTime.plus(diff)
                                        eventStartTime = it.timeStamp
                                        eventEndTime = 0L
                                        eventEndTime = 0L
                                    }
                                }
                            }
                        }
                    }

                    Log.e("UsageTime", spentTime.toString())
                    resultCallback.invoke(
                        spentTime,
                        null
                    )

                } else {
                    resultCallback.invoke(
                        0,
                        null
                    )
                }
            } else {
                resultCallback.invoke(null, "App not installed")
            }
        } else {
            resultCallback.invoke(null, "Package not found")
        }


    }


    private fun checkUsageStatsPermission(): Boolean {

        // AppOpsManager.checkOpNoThrow` is deprecated from Android Q
        val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            appOpsManager?.unsafeCheckOpNoThrow(
                "android:get_usage_stats",
                Process.myUid(), Monetization.packageName
            )
        } else {
            appOpsManager?.checkOpNoThrow(
                "android:get_usage_stats",
                Process.myUid(), Monetization.packageName
            )
        }
        return mode == AppOpsManager.MODE_ALLOWED
    }


    fun getInstalledDurationInSeconds(onResult: (installDurationInSec: Long?, errorMessage: String?) -> Unit) {
        packageToCheck?.let {
            if (it.isPackageInstalled(context)) {
                val packageInfo = it.getPackageInfo(packageManager)
                if (packageInfo != null) {
                    val installTimeStamp = packageInfo.firstInstallTime
                    val diffInSeconds = installTimeStamp.getSecondsDiffBetweenCurrentDate()
                    onResult.invoke(diffInSeconds, null)
                } else {
                    onResult.invoke(null, "package was removed.")
                }
            } else {
                onResult.invoke(null, "package was removed.")
            }

        }

    }

}