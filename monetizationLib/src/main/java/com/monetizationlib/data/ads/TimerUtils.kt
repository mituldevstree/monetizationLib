package com.monetizationlib.data.ads

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper


object TimerUtils {

    private val lock = Any()

    @Volatile
    private var mainHandler: Handler? = null

    @Volatile
    private var backgroundHandler: Handler? = null

    fun postDelayedInUIThread(runnable: Runnable, delayMillis: Long): Runnable? {
        return postDelayed(getMainHandler(), runnable, delayMillis)
    }

    @JvmStatic
    fun postDelayedInBackgroundThread(runnable: Runnable, delayMillis: Long): Runnable? {
        return postDelayed(getBackgroundHandler(), runnable, delayMillis)
    }

    private fun getMainHandler(): Handler? {
        if (mainHandler == null) {
            synchronized(lock) {
                mainHandler = Handler(Looper.getMainLooper())
            }
        }
        return mainHandler
    }

    fun getBackgroundHandler(): Handler? {
        if (backgroundHandler == null) {
            synchronized(lock) {
                val mHandlerThread = HandlerThread("HandlerThread")
                mHandlerThread.start()
                backgroundHandler = Handler(mHandlerThread.looper)
            }
        }
        return backgroundHandler
    }

    private fun postDelayed(handler: Handler?, runnable: Runnable, delayMillis: Long): Runnable? {
        if (handler == null) {
            return null
        }

        try {
            val errorHandlingRunnable = {
                try {
                    runnable.run()
                } catch (throwable: Throwable) {
                }
            }
            handler.postDelayed(errorHandlingRunnable, delayMillis)

            return Runnable(errorHandlingRunnable)
        } catch (throwable: Throwable) {
        }

        return null
    }

    fun removeCallbacks(runnable: Runnable) {
        try {
            removeCallbacks(getMainHandler(), runnable)
            removeCallbacks(getBackgroundHandler(), runnable)
        } catch (throwable: Throwable) {
        }
    }

    private fun removeCallbacks(handler: Handler?, runnable: Runnable?) {
        if (handler == null) {
            return
        }

        // If runnable is null everything is removed so we don't want that..!
        if (runnable == null) {
            return
        }
        handler.removeCallbacks(runnable)
    }

    fun removeAllCallbacks() {
        if (backgroundHandler != null) {
            backgroundHandler!!.removeCallbacks(null!!)
        }
        if (mainHandler != null) {
            mainHandler!!.removeCallbacks(null!!)
        }
    }

    fun cleanUp() {
        if (mainHandler != null || backgroundHandler != null) {
            synchronized(lock) {
                removeAllCallbacks()
                mainHandler = null
                backgroundHandler = null
            }
        }
    }
}
