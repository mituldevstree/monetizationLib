package com.monetizationlib.data.base.view

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.monetizationlib.data.Monetization
import com.monetizationlib.data.R
import com.monetizationlib.data.base.view.adapter.StepsAdapter

open class AppInstallRewardAlertDialog(context: Context?) : BaseDialogHelper() {

    constructor(
        context: Context?,
        onButtonClick: (AppInstallRewardAlertDialog) -> Unit = {},
    ) : this(context) {
        val totalSteps =
            Monetization.monetizationConfig?.getDownloadProgressViewAppsCount()?.toIntOrNull()
                ?: 7
        val completedSteps =
            Monetization.monetizationConfig?.getDownloadProgressViewCurrentAppsCount()
                ?.toIntOrNull()
        rvSteps.adapter = StepsAdapter(totalSteps, completedSteps)
        descriptionTextView.text =
            Monetization.monetizationConfig?.downloadProgressViewTitle
                ?: context?.getString(R.string.how_to_win)
        additionalRewardsTextView.text =
            Monetization.monetizationConfig?.downloadProgressViewDesc
                ?: context?.getString(R.string.download_5_apps_which_show_when_you_are_watching_an_ad)
        gotIt.text = Monetization.monetizationConfig?.downloadProgressViewButtonText
            ?: context?.getString(R.string.watch_now)
        tvEarnings.text = Monetization.monetizationConfig?.downloadProgressViewReward() ?: ""

        okayButtonClickListener {
            onButtonClick(this)
        }

        cancelButtonClickListener{}
    }

    override var cancelable: Boolean = true

    //  dialog view
    final override val dialogView: View by lazy {
        LayoutInflater.from(context).inflate(R.layout.dialog_app_install_reward_info, null)
    }

    override val builder: AlertDialog.Builder = AlertDialog.Builder(context).setView(dialogView)

    //  title text
    private val descriptionTextView: AppCompatTextView by lazy {
        dialogView.findViewById(R.id.descriptionTextView)
    }

    //  title text
    private val additionalRewardsTextView: AppCompatTextView by lazy {
        dialogView.findViewById(R.id.additionalRewardsTextView)
    }

    //  title text
    private val tvEarnings: AppCompatTextView by lazy {
        dialogView.findViewById(R.id.tvEarnings)
    }

    //  okay button
    private val gotIt: AppCompatButton by lazy {
        dialogView.findViewById(R.id.gotIt)
    }

    //  title text
    private val rvSteps: RecyclerView by lazy {
        dialogView.findViewById(R.id.rvSteps)
    }

    //  okay button
    private val ivClose: AppCompatImageView by lazy {
        dialogView.findViewById(R.id.ivClose)
    }

    fun okayButtonClickListener(func: (() -> Unit)? = null) =
        with(gotIt) {
            setClickListenerToDialogButton(func)
        }

    fun cancelButtonClickListener(func: (() -> Unit)? = null) =
        with(ivClose) {
            setClickListenerToDialogButton(func)
        }

    private fun View.setClickListenerToDialogButton(func: (() -> Unit)?) = setOnClickListener {
        func?.invoke()
        dialog?.dismiss()
    }

    private fun View.setCloseClickListenerToDialogButton() = setOnClickListener {
        dialog?.dismiss()
    }
}