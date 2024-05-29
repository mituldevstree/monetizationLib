package com.monetizationlib.data.attributes.businessModule

import android.content.Context
import android.content.res.Configuration
import android.graphics.Point
import android.view.WindowManager

object ScreenUtil {
    fun getPixelsFromDp(context: Context, dp: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }

    fun getScreenSize(context: Context): Point {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val size = Point()
        display.getSize(size)
        return size
    }

    fun isSmallDevice(context: Context?): Boolean {
        if (context == null) return false
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val height = wm.defaultDisplay.height
        val screenSize = context.resources.configuration.screenLayout and
                Configuration.SCREENLAYOUT_SIZE_MASK
        return Configuration.SCREENLAYOUT_SIZE_SMALL == screenSize || height < 900
    }
}