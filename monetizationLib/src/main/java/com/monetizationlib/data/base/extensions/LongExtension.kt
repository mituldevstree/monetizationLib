package com.monetizationlib.data.base.extensions

import java.util.Locale
import java.util.concurrent.TimeUnit


fun Long.convertToHHMMSS(): String {
    val s = this % 60
    val m = this / 60 % 60
    val h = this / (60 * 60) % 24
    val d = this / (60 * 60 * 24) % 365
    return if (h > 0) {
        String.format(Locale.ENGLISH, "%02d:%02d:00", h, m)
    } else if (m > 0) {
        String.format(Locale.ENGLISH, "00:%02d:%02d", m, s)
    } else {
        String.format(Locale.ENGLISH, "00:00:%02d", s)
    }
}

fun Long.getSecondsDiffBetweenCurrentDate(): Long {
    val diffInMs: Long = System.currentTimeMillis().minus(this)
    return if (diffInMs <= 0) 0
    else
        TimeUnit.MILLISECONDS.toSeconds(diffInMs)
}

fun Long.getSecondsDiffBetweenDate(startTimeStamp:Long): Long {
    val diffInMs: Long = this.minus(startTimeStamp)
    return if (diffInMs <= 0) 0
    else
        TimeUnit.MILLISECONDS.toSeconds(diffInMs)
}