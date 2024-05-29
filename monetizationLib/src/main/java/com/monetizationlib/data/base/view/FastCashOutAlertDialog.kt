package com.monetizationlib.data.base.view

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import com.google.firebase.analytics.FirebaseAnalytics
import com.monetizationlib.data.Monetization
import com.monetizationlib.data.R
import com.monetizationlib.data.ads.model.EligibleForRewardResponse
import com.monetizationlib.data.attributes.businessModule.AnimUtil
import com.monetizationlib.data.base.extensions.hideKeyboard

class FastCashOutAlertDialog(context: Context?) : BaseDialogHelper() {

    companion object {
        var isAlreadyShown: Boolean = false
        var isOnceShown: Boolean = false
    }

    private var isOptionSelected: Boolean = false
    private var provider: String = "coinbase"

    constructor(
        context: Context?,
        eligibleForRewardResponse: EligibleForRewardResponse,
        continueAction: (email: String, provider: String) -> Unit,
    ) : this(context) {
        isOnceShown = true
        isAlreadyShown = true

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
        firebaseAnalytics?.logEvent("fast_cash_out_dialog_shown", params)

        titleTextView.text = Monetization.monetizationConfig?.fastRewardConfig?.fastCashOutTitleOne
            ?: context?.resources?.getString(R.string.first_cash_out)
        descTextView.text = Monetization.monetizationConfig?.fastRewardConfig?.fastCashOutDescOne
            ?: context?.resources?.getString(R.string.you_are_a_few_steps_away_from_your_first_cash_out)

        if (eligibleForRewardResponse.canCashoutWithPayeer) {
            payeerButton.visibility = VISIBLE
            orTextView.visibility = VISIBLE
        } else {
            payeerButton.visibility = GONE
            orTextView.visibility = GONE
        }

        coinbaseButton.setOnClickListener {
            context?.resources?.let { resources ->
                isOptionSelected = true
                provider = "coinbase"
                coinbaseButton.setBackgroundResource(R.drawable.background_fast_cashout_selected)
                payeerButton.setBackgroundResource(R.drawable.background_fast_cashout_empty)
                emailEditText.setText("")
                emailEditText.visibility = VISIBLE
                emailEditText.hint = resources.getString(R.string.enter_your_email)
            }
        }

        payeerButton.setOnClickListener {
            context?.resources?.let { resources ->
                isOptionSelected = true
                provider = "payeer"
                payeerButton.setBackgroundResource(R.drawable.background_fast_cashout_selected)
                coinbaseButton.setBackgroundResource(R.drawable.background_fast_cashout_empty)
                emailEditText.setText("")
                emailEditText.visibility = VISIBLE
                emailEditText.hint =resources.getString(R.string.enter_your_account_number)
            }
        }

        emailEditText.imeOptions = EditorInfo.IME_ACTION_DONE
        emailEditText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                emailEditText.hideKeyboard()
                true
            } else {
                false
            }
        }

        emailEditText.setOnFocusChangeListener { v, hasFocus ->
//            if (eligibleForRewardResponse.canCashoutWithPayeer) {
//                if (hasFocus) {
//                    titleTextView.visibility = GONE
//                    descTextView.visibility = GONE
//                } else {
//                    titleTextView.visibility = VISIBLE
//                    descTextView.visibility = VISIBLE
//                }
//            }
        }

        okayButton.setOnClickListener {
            if (!isOptionSelected) {
                AnimUtil.shakeViews(500, coinbaseButton, payeerButton)
                return@setOnClickListener
            }

            if (emailEditText.text.toString().isBlank()
                || emailEditText.text.toString().isEmpty()
            ) {
                AnimUtil.shakeView(emailEditText)
                return@setOnClickListener
            }

            params.putString("email", emailEditText.text.toString())
            params.putString("provider", provider)
            firebaseAnalytics?.logEvent("fast_cash_out_dialog_continue_clicked", params)
            continueAction(emailEditText.text.toString(), provider)
            isAlreadyShown = false
            dialog?.dismiss()
        }

        cancelButtonClickLIstener {
            firebaseAnalytics?.logEvent("fast_cash_out_dialog_cancel_clicked", params)
        }
    }

    override var cancelable: Boolean = false

    //  dialog view
    override val dialogView: View by lazy {
        LayoutInflater.from(context).inflate(R.layout.fast_cash_out_alert_dialog, null)
    }

    override val builder: AlertDialog.Builder = AlertDialog.Builder(context).setView(dialogView)

    //  titleTextView
    private val titleTextView: TextView by lazy {
        dialogView.findViewById(R.id.titleTextView)
    }

    //  descTextView
    private val descTextView: TextView by lazy {
        dialogView.findViewById(R.id.descTextView)
    }

    //  orTextView
    private val orTextView: TextView by lazy {
        dialogView.findViewById(R.id.orTextView)
    }

    //  emailEditText
    private val emailEditText: EditText by lazy {
        dialogView.findViewById(R.id.emailEditText)
    }

    //  okay button
    private val okayButton: Button by lazy {
        dialogView.findViewById(R.id.okayButton)
    }

    //  cancel button
    private val cancelButton: ImageButton by lazy {
        dialogView.findViewById(R.id.closeButton)
    }

    //  coinbaseButton
    private val coinbaseButton: ImageButton by lazy {
        dialogView.findViewById(R.id.coinbaseButton)
    }

    // payeerButton
    private val payeerButton: ImageButton by lazy {
        dialogView.findViewById(R.id.payeerButton)
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