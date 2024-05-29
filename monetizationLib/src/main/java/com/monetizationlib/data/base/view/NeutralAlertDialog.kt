package com.monetizationlib.data.base.view

import android.app.AlertDialog
import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.monetizationlib.data.R
import com.monetizationlib.data.base.view.customviews.GivvyButton

class NeutralAlertDialog(context: Context?) : BaseDialogHelper() {

    constructor(
        context: Context?,
        message: String,
        okayButtonTitle: String,
        showSecondButton: Boolean,
        cancelButtonTitle: String,
        cancelable: Boolean = false,
        okayAction: (NeutralAlertDialog) -> Unit = {},
        cancelAction: (NeutralAlertDialog) -> Unit = {},
        drawable: Drawable? = null
    ) : this(context) {
        messageTextView.text = Html.fromHtml(message)
        messageTextView.movementMethod = LinkMovementMethod.getInstance()
        okayButton.text = okayButtonTitle
        if (drawable != null) {
            imageView.setImageDrawable(drawable)
            imageView.visibility = View.VISIBLE
        }
        when (showSecondButton) {
            true -> {
                cancelButton.visibility = View.VISIBLE
                cancelButton.text = cancelButtonTitle
            }
            else -> cancelButton.visibility = View.GONE
        }
        this.cancelable = cancelable
        okayButtonClickListener {
            okayAction(this)
        }

        cancelButtonClickListener {
            cancelAction(this)
        }
    }

    override var cancelable: Boolean = false

    //  dialog view
    override val dialogView: View by lazy {
        LayoutInflater.from(context).inflate(R.layout.fragment_neutral_dialog, null)
    }

    override val builder: AlertDialog.Builder = AlertDialog.Builder(context).setView(dialogView)

    //  message text view
    private val messageTextView: TextView by lazy {
        dialogView.findViewById(R.id.messageTextView)
    }

    //  okay button
    private val okayButton: Button by lazy {
        dialogView.findViewById(R.id.okayButton)
    }

    private val imageView: ImageView by lazy {
        dialogView.findViewById<ImageView>(R.id.image)
    }

    //  okay button
    private val cancelButton: Button by lazy {
        dialogView.findViewById(R.id.cancelButton)
    }

    //  cancelButtonClickListener with listener
    fun cancelButtonClickListener(func: (() -> Unit)? = null) =
        with(cancelButton) {
            setClickListenerToDialogButton(func)
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