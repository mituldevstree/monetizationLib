package com.monetizationlib.data.base.view.utility

import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import java.util.concurrent.TimeUnit

class TimerUtils(private val lifecycle: Lifecycle) : LifecycleObserver, LifecycleEventObserver {

    var activity: AppCompatActivity? = null
    private var countDownInterval: Long = 1000
    private var mListeners: TimerListeners? = null
    var millisUntilFinished: Long = 0
    private var mHandler: Handler? = null
    private var mRunnable: Runnable? = null
    private var isRunning: Boolean = false
    private var hasFinished: Boolean = false

    fun setMillisInFuture(millisInFuture: Long): TimerUtils {
        millisUntilFinished = millisInFuture
        return this
    }

    fun setCountDownInterval(countDownInterval: Long): TimerUtils {
        this.countDownInterval = countDownInterval
        return this
    }

    fun setTimerUpdateListener(mListeners: TimerListeners): TimerUtils {
        this.mListeners = mListeners
        return this
    }

    fun build(): TimerUtils {
        initTimer()
        lifecycle.addObserver(this)
        return this
    }

    private fun initTimer() {
        mHandler = Handler(Looper.getMainLooper())
        mRunnable = Runnable {
            if (millisUntilFinished > 0) {
                hasFinished = false
//                val day = TimeUnit.MILLISECONDS.toDays(millisUntilFinished)
                val hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished)
                val minutes =
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(
                        TimeUnit.MILLISECONDS.toHours(
                            millisUntilFinished
                        )
                    )
                val seconds =
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(
                            millisUntilFinished
                        )
                    )
                mListeners?.onTimerUpdate(hours, minutes, seconds)
                mListeners?.onLeftTime(millisUntilFinished)
                millisUntilFinished -= countDownInterval
                if (isRunning) startCallBack()
            } else {
                if (hasFinished.not()) {
                    hasFinished = true
                    finishTimer()
                }
            }
        }
    }

    private fun removeCallBack() {
        mRunnable?.let { mHandler?.removeCallbacks(it) }
    }

    private fun startCallBack() {
        mRunnable?.let { mHandler?.postDelayed(it, countDownInterval) }
    }

    fun destroyTimer() {
        isRunning = false
        removeCallBack()
        mListeners = null
    }

    fun finishTimer() {
        mListeners?.onTimerFinish()
        destroyTimer()
    }

    fun pauseTimer() {
        isRunning = false
        removeCallBack()
    }

    fun resumeTimer() {
        isRunning = true
        removeCallBack()
        startCallBack()
    }

    fun isTimerRunning(): Boolean {
        return isRunning
    }

    fun startTimer() {
        removeCallBack()
        isRunning = true
        startCallBack()
    }

    fun updateTime(updateTime: Long) {
        removeCallBack()
        isRunning = true
        millisUntilFinished = updateTime
        startCallBack()
    }


    interface TimerListeners {
        fun onTimerUpdate(hours: Long, minutes: Long, seconds: Long)
        fun onLeftTime(timeLeft: Long) {}
        fun onTimerFinish()
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                if (isRunning.not()) {
                    resumeTimer()
                }
            }

            Lifecycle.Event.ON_PAUSE -> {
                pauseTimer()
            }

            Lifecycle.Event.ON_STOP -> {
                pauseTimer()
            }

            Lifecycle.Event.ON_DESTROY -> {
                destroyTimer()
            }

            else -> {

            }
        }
    }

}