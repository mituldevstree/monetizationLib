package com.monetizationlib.data.attributes.businessModule

import android.animation.Animator
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.LinearInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationUtils

object AnimUtil {
    fun shakeView(view: View, duration: Long = 500) {
        val step1 = 25f
        val step2 = 10f
        val step3 = 6f
        val step4 = 3f

        val translationX = ObjectAnimator.ofFloat(
            view, View.TRANSLATION_X,
            step1, -step1, step2, -step2, step3, -step3, step4, -step4, 0f
        )
        translationX.duration = duration
        translationX.interpolator = LinearInterpolator()
        translationX.start()
    }

    fun smallShakeView(view: View, duration: Long = 500) {
        val step2 = 10f
        val step3 = 6f
        val step4 = 3f

        val translationX = ObjectAnimator.ofFloat(
            view, View.TRANSLATION_X,
            step2, -step2, step3, -step3, step4, -step4, 0f
        )
        translationX.duration = duration
        translationX.interpolator = LinearInterpolator()
        translationX.start()
    }

    fun shakeViews(duration: Long = 500, vararg views: View) {
        for (view: View in views) {
            shakeView(view, duration)
        }
    }

    fun animate(animResId: Int, vararg views: View) {
        if (views.isEmpty()) return

        val animation = AnimationUtils.loadAnimation(views[0].context, animResId)

        for (view in views) {
            runAnimation(view, animation)
        }
    }

    fun animate(animResId: Int, views: ArrayList<View>) {
        if (views.isEmpty()) return

        val animation = AnimationUtils.loadAnimation(views[0].context, animResId)

        for (view in views) {
            runAnimation(view, animation)
        }
    }

    private fun runAnimation(view: View?, animation: Animation) {
        if (view == null) {
            return
        }

        view.startAnimation(animation)
    }

    fun translateViewToX(view: View, step: Float) {
        val translationX = ObjectAnimator.ofFloat(view, View.TRANSLATION_X, step)
        translationX.duration = 200
        translationX.interpolator = LinearInterpolator()
        translationX.start()
    }

    open class DefaultAnimatorListener : Animator.AnimatorListener {

        override fun onAnimationRepeat(animation: Animator) {
        }

        override fun onAnimationEnd(animation: Animator) {
        }

        override fun onAnimationCancel(animation: Animator) {
        }

        override fun onAnimationStart(animation: Animator) {
        }

    }
}