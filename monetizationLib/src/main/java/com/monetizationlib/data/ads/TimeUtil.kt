package com.monetizationlib.data.ads

import android.os.SystemClock


object TimeUtil {
    const val ONE_SECOND: Long = 1000
    const val ONE_MINUTE = 60 * ONE_SECOND
    const val ONE_HOUR = 60 * ONE_MINUTE
    const val ONE_DAY = 24 * ONE_HOUR
    const val ONE_WEEK = 7 * ONE_DAY

    @JvmStatic
    fun hasTimeoutExpired(
        startTimeNanos: Long,
        defaultWhenUndefined: Boolean,
        timeoutMillis: Long
    ): Boolean {
        if (startTimeNanos < 0) {
            return defaultWhenUndefined
        }
        val timeElapsed = getTimeElapsed(startTimeNanos)
        return timeElapsed > timeoutMillis
    }
    @JvmStatic
    fun getTimeElapsed(startTimeNanos: Long): Long {
        return toMilliseconds(System.nanoTime() - startTimeNanos)
    }

    fun toMilliseconds(nanoTime: Long): Long {
        return Math.round(nanoTime.toDouble() * 0.000001)
    }

    fun hasTimePassed(time: Long, threshold: Long): Boolean {
        return SystemClock.elapsedRealtime() - time < threshold
    }

    fun formatSeconds(timeInSeconds: Long): String {
        val hours = timeInSeconds / 3600
        val secondsLeft = timeInSeconds - hours * 3600
        val minutes = secondsLeft / 60
        val seconds = secondsLeft - minutes * 60

        var formattedTime = ""
        if (hours < 10)
            formattedTime += "0"
        formattedTime += "$hours:"

        if (minutes < 10)
            formattedTime += "0"
        formattedTime += "$minutes:"

        if (seconds < 10)
            formattedTime += "0"
        formattedTime += seconds

        return formattedTime
    }
}