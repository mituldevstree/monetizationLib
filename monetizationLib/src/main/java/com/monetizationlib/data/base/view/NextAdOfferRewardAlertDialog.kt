package com.monetizationlib.data.base.view

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.monetizationlib.data.AdRewardType
import com.monetizationlib.data.Monetization
import com.monetizationlib.data.R

class NextAdOfferRewardAlertDialog(context: Context?) : BaseDialogHelper() {

    companion object {
        var isAlreadyShown = false
    }
    constructor(
        context: Context?,
        okayAction: (NextAdOfferRewardAlertDialog) -> Unit = {},
    ) : this(context) {

        when (Monetization.adRewardType) {
            AdRewardType.NEXT_AD_FEATURE -> {
                titleTextView.text =  Monetization.monetizationConfig?.redeemTitleForNextAdOffer ?: context?.getString(R.string.congratulations)
                coinsHolder.visibility = View.VISIBLE
                coinsRewardTextView.text =  Monetization.monetizationConfig?.redeemCoinsForNextAdOffer ?: "4 000"
                descTextView.text =  Monetization.monetizationConfig?.redeemDescForNextAdOffer ?: context?.getString(R.string.thanks_for_downloading_the_app)
                okayButton.text = context?.getString(R.string.Continue)
            }
            AdRewardType.DOWNLOAD_APPS_FEATURE -> {
                titleTextView.text =  Monetization.monetizationConfig?.downloadRedeemTitleForNextAdOffer ?: context?.getString(R.string.congratulations)
                coinsHolder.visibility = View.GONE
                descTextView.text =  Monetization.monetizationConfig?.downloadRedeemDescForNextAdOffer ?: context?.getString(R.string.thanks_for_downloading_the_app_download_feature)
                okayButton.text = context?.getString(R.string.Continue)
            }
            else -> {}
        }

        isAlreadyShown = true
        okayButtonClickListener {
            okayAction(this)
        }
    }

    override var cancelable: Boolean = false

    //  dialog view
    override val dialogView: View by lazy {
        LayoutInflater.from(context).inflate(R.layout.dialog_bonus_reward, null)
    }

    override val builder: AlertDialog.Builder = AlertDialog.Builder(context).setView(dialogView)

    //  previousPercentageTextView text
    private val titleTextView: AppCompatTextView by lazy {
        dialogView.findViewById(R.id.videoWatchedReward)
    }

    //  title text
    private val coinsRewardTextView: AppCompatTextView by lazy {
        dialogView.findViewById(R.id.bonusRewardTextView)
    }

    // coinsHolder
    private val coinsHolder: ConstraintLayout by lazy {
        dialogView.findViewById(R.id.coinsHolder)
    }

    //  title text
    private val descTextView: AppCompatTextView by lazy {
        dialogView.findViewById(R.id.watchVideoTextView)
    }

    //  okay button
    private val okayButton: AppCompatButton by lazy {
        dialogView.findViewById(R.id.okayButton)
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
            isAlreadyShown = false
            dialog?.dismiss()
        }
}