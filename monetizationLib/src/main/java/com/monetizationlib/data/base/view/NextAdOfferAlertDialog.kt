package com.monetizationlib.data.base.view

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import com.monetizationlib.data.Monetization
import com.monetizationlib.data.R

class NextAdOfferAlertDialog(context: Context?) : BaseDialogHelper() {

    companion object {
        var isShownOnceAlready: Boolean = false
    }
    constructor(
        context: Context?,
        okayAction: (NextAdOfferAlertDialog) -> Unit = {},
    ) : this(context) {
        isShownOnceAlready = true
        titleTextView.text = Monetization.monetizationConfig?.titleForNextAdOffer ?:  context?.getString(R.string.big_reward)
        coinsRewardTextView.text =  Monetization.monetizationConfig?.coinsForNextAdOffer ?: "4 000 COINS"
        descTextView.text = Monetization.monetizationConfig?.descForNextAdOffer ?: context?.getString(R.string.a_special_offer_for_next_ad)
        okayButton.text = context?.getString(R.string.watch_ad_title)?: "Watch now"
        okayButtonClickListener {
            okayAction(this)
        }

        closeButton.setOnClickListener {
            dialog?.dismiss()
        }
    }

    override var cancelable: Boolean = false

    //  dialog view
    override val dialogView: View by lazy {
        LayoutInflater.from(context).inflate(R.layout.big_reward_alert_dialog, null)
    }

    override val builder: AlertDialog.Builder = AlertDialog.Builder(context).setView(dialogView)

    //  title text
    private val coinsRewardTextView: AppCompatTextView by lazy {
        dialogView.findViewById(R.id.coinsRewardTextView)
    }

    //  title text
    private val descTextView: AppCompatTextView by lazy {
        dialogView.findViewById(R.id.descTextView)
    }

    //  title text
    private val titleTextView: AppCompatTextView by lazy {
        dialogView.findViewById(R.id.titleTextView)
    }

    //  okay button
    private val okayButton: AppCompatButton by lazy {
        dialogView.findViewById(R.id.okayButton)
    }

    //  okay button
    private val closeButton: ImageButton by lazy {
        dialogView.findViewById(R.id.closeButton)
    }

    //  okayButtonClickListener with listener
    fun okayButtonClickListener(func: (() -> Unit)? = null) =
        with(okayButton) {
            setClickListenerToDialogButton(func)
        }

    //  view click listener as extension function
    private fun View.setClickListenerToDialogButton(func: (() -> Unit)?) =
        setOnClickListener {
            func?.invoke()
            dialog?.dismiss()
        }
}