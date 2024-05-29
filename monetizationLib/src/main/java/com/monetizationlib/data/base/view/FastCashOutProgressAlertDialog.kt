package com.monetizationlib.data.base.view

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import com.airbnb.lottie.LottieAnimationView
import com.google.firebase.analytics.FirebaseAnalytics
import com.monetizationlib.data.Monetization
import com.monetizationlib.data.R

class FastCashOutProgressAlertDialog(context: Context?) : BaseDialogHelper() {

    companion object {
        var isAlreadyShown: Boolean = false
        var isInBigRewardWithCashOutAdFlow: Boolean = false
        private var currentNumberOfAds: Int = 1
        private var maxNumberOfAds: Int = 3
    }

    constructor(
        context: Context?,
        continueAction: (isFinalStep: Boolean) -> Unit,
    ) : this(context) {
        isAlreadyShown = true
        val firebaseAnalytics = context?.let { FirebaseAnalytics.getInstance(it) }
        val params = Bundle().apply {
            putString("packageName", Monetization.packageName)
            putString("userId", Monetization.userId)
        }
        firebaseAnalytics?.logEvent("fast_cash_out_progress_dialog_shown", params)

        cancelButtonClickLIstener {
            firebaseAnalytics?.logEvent("fast_cash_out_progress_cancel_clicked", params)
            isInBigRewardWithCashOutAdFlow = false
        }

        titleTextView.text = Monetization.monetizationConfig?.fastRewardConfig?.fastCashOutTitleTwo ?: context?.resources?.getString(R.string.first_cash_out)

        maxNumberOfAds = Monetization.monetizationConfig?.fastRewardConfig?.numberOfMaxAds ?: maxNumberOfAds
        val currentProgress = maxNumberOfAds - currentNumberOfAds
        progressBarTextTextView.text =
            context?.getString(R.string.current_ads_progress)?.format(currentNumberOfAds, maxNumberOfAds)
        progressBar.max = maxNumberOfAds
        progressBar.progress = currentNumberOfAds

        if(Monetization.monetizationConfig?.fastRewardConfig?.fastCashOutDescTwo.isNullOrEmpty()) {
            descTextView.text =
                context?.getString(R.string.ads_away_from_reward)?.format(currentProgress)
        } else {
            Monetization.monetizationConfig?.fastRewardConfig?.fastCashOutDescTwo?.let { fastCashOutDescTwoWrapped ->
                if(fastCashOutDescTwoWrapped.contains("%d")){
                    descTextView.text = fastCashOutDescTwoWrapped.format(currentProgress)
                } else {
                    descTextView.text = fastCashOutDescTwoWrapped
                }
            }
        }

        val isFinalStep = currentNumberOfAds == maxNumberOfAds
        if(isFinalStep) {
            confettiAnimation.playAnimation()
            titleTextView.text = Monetization.monetizationConfig?.fastRewardConfig?.fastCashOutTitleLast ?: context?.resources?.getString(R.string.congratulations)
            descTextView.text = Monetization.monetizationConfig?.fastRewardConfig?.fastCashOutDescLast ?: context?.resources?.getString(R.string.money_are_sent)
            params.putBoolean("isFinalStep", isFinalStep)
            firebaseAnalytics?.logEvent("fast_cash_out_progress_final_step", params)
        }

        continueButtonClickLIstener {
            if(isFinalStep) {
                isInBigRewardWithCashOutAdFlow = false
            }
            firebaseAnalytics?.logEvent("fast_cash_out_progress_continue_clicked", params)
            currentNumberOfAds++
            continueAction(isFinalStep)
        }
    }

    override var cancelable: Boolean = false

    //  dialog view
    override val dialogView: View by lazy {
        LayoutInflater.from(context)
            .inflate(R.layout.fast_cash_out_progress_alert_dialog, null)
    }

    override val builder: AlertDialog.Builder = AlertDialog.Builder(context).setView(dialogView)

    //  confettiAnimation
    private val confettiAnimation: LottieAnimationView by lazy {
        dialogView.findViewById(R.id.confettiAnimation)
    }

    //  descTextView
    private val titleTextView: TextView by lazy {
        dialogView.findViewById(R.id.titleTextView)
    }

    //  descTextView
    private val descTextView: TextView by lazy {
        dialogView.findViewById(R.id.descTextView)
    }

    //  progressBarTextTextView
    private val progressBarTextTextView: TextView by lazy {
        dialogView.findViewById(R.id.progressBarTextTextView)
    }

    //  progressBarTextTextView
    private val progressBar: ProgressBar by lazy {
        dialogView.findViewById(R.id.progressBar)
    }

    //  okay button
    private val okayButton: Button by lazy {
        dialogView.findViewById(R.id.okayButton)
    }

    //  cancel button
    private val cancelButton: ImageButton by lazy {
        dialogView.findViewById(R.id.closeButton)
    }

    //  okayButtonClickListener with listener
    private fun continueButtonClickLIstener(func: (() -> Unit)? = null) =
        with(okayButton) {
            setClickListenerToDialogButton(func)
        }

    //  okayButtonClickListener with listener
    private fun cancelButtonClickLIstener(func: (() -> Unit)? = null) =
        with(cancelButton) {
            setClickListenerToDialogButton(func)
        }

    //  view click listener as extension function
    private fun View.setClickListenerToDialogButton(func: (() -> Unit)?) =
        setOnClickListener {
            func?.invoke()
            isAlreadyShown = false
            dialog?.dismiss()
        }
}