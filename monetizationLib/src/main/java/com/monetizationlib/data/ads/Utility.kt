package com.monetizationlib.data.ads

import android.os.Handler
import android.os.Looper


object Utility {

    val isMainThread: Boolean
        get() {
            return try {
                Looper.myLooper() == Looper.getMainLooper()
            } catch (throwable: Throwable) {
                false
            }
        }

    @JvmStatic
    fun executeOnUIThread(action: Runnable) {
        var action = action
        action = createRunnable(action)
        if (isMainThread) {
            action.run()
        } else {
            Handler(Looper.getMainLooper()).post(action)
        }
    }

    private fun createRunnable(runnable: Runnable): Runnable {
        return Runnable {
            try {
                runnable.run()
            } catch (e: RuntimeException) {
            }
        }
    }

    @JvmStatic
    fun executeOnBGThread(action: Runnable) {
        var action = action
        action = createRunnable(action)
        if (isMainThread) {
            Threading.getInstance().execute(action)
        } else {
            action.run()
        }
    }
}
