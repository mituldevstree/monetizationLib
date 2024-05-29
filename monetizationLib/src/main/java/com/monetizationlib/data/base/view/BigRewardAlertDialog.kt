package com.monetizationlib.data.base.view

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import com.google.firebase.analytics.FirebaseAnalytics
import com.monetizationlib.data.Monetization
import com.monetizationlib.data.R
import com.monetizationlib.data.ads.model.EligibleForRewardResponse

class BigRewardAlertDialog(context: Context?) : BaseDialogHelper() {

    companion object {
        var isAlreadyShown: Boolean = false
        var isInBigRewardAdFlow: Boolean = false
    }

    constructor(
        context: Context?,
        eligibleForRewardResponse: EligibleForRewardResponse,
        continueAction: () -> Unit = {},
        shownAction: () -> Unit = {},
    ) : this(context) {
        isAlreadyShown = true
        shownAction()
        val firebaseAnalytics = context?.let { FirebaseAnalytics.getInstance(it) }
        val params = Bundle().apply {
            putString("packageName", Monetization.packageName)
            putString("userId", Monetization.userId)
            putString(
                "expectedRewardInCredits",
                eligibleForRewardResponse.expectedRewardInCredits.toString()
            )
            putString("expectedRewardInCredits", eligibleForRewardResponse.expectedReward)
        }
        firebaseAnalytics?.logEvent("big_reward_dialog_shown", params)

        titleTextView.text = Monetization.monetizationConfig?.fastRewardConfig?.bigRewardTitleOne
            ?: context?.resources?.getString(R.string.big_reward)
        coinsRewardTextView.text = context?.getString(R.string.coins_placeholder)
            ?.format(eligibleForRewardResponse.expectedRewardInCredits.toString())
        descTextView.text = Monetization.monetizationConfig?.fastRewardConfig?.bigRewardDescOne
            ?: context?.resources?.getString(R.string.a_big_special_reward_is_here_for_you)

        continueButtonClickLIstener {
            firebaseAnalytics?.logEvent("big_reward_dialog_okay_clicked", params)
            continueAction()
        }

        cancelButtonClickLIstener {
            firebaseAnalytics?.logEvent("big_reward_dialog_cancel_clicked", params)
        }
    }

    override var cancelable: Boolean = false

    //  dialog view
    override val dialogView: View by lazy {
        LayoutInflater.from(context).inflate(R.layout.big_reward_alert_dialog, null)
    }

    override val builder: AlertDialog.Builder = AlertDialog.Builder(context).setView(dialogView)

    //  title text view
    private val titleTextView: TextView by lazy {
        dialogView.findViewById(R.id.titleTextView)
    }

    //  coins reward text view
    private val coinsRewardTextView: TextView by lazy {
        dialogView.findViewById(R.id.coinsRewardTextView)
    }

    //  desc text view
    private val descTextView: TextView by lazy {
        dialogView.findViewById(R.id.descTextView)
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