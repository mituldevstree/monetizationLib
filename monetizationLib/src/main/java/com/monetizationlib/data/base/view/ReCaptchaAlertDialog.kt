package com.monetizationlib.data.base.view

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.monetizationlib.data.Monetization
import com.monetizationlib.data.R
import com.monetizationlib.data.base.view.customviews.GivvyTextView
import com.ncorti.slidetoact.SlideToActView

class ReCaptchaAlertDialog(context: Context?) : BaseDialogHelper() {

    companion object {
        var isAlreadyShown: Boolean = false
    }

    constructor(
        context: Context?,
        continueAction: () -> Unit = {},
        shownAction: () -> Unit = {},
    ) : this(context) {
        if (Monetization.needToShowSwipeCheck != true) {
            dialog?.dismiss()
            isAlreadyShown = false
        } else {
            /** Never change that WITHOUT testing, i don't know why, but i spend more than 2 hours to make a stupid random position.
            This was the only thing working when the aar is applied and called from another app.
            It must be called in the constructor and pass down to the methods.
             */
            val shuffledPosition = (0..2).shuffled().last()
            setupForSectionOffer(context, continueAction, shownAction, shuffledPosition)
        }
    }

    private fun setupForSectionOffer(
        context: Context?,
        continueAction: () -> Unit = {},
        shownAction: () -> Unit = {},
        shuffledPosition: Int
    ) {
        placeViews(shuffledPosition)
        slider.onSlideCompleteListener = object : SlideToActView.OnSlideCompleteListener {
            override fun onSlideComplete(view: SlideToActView) {
                continueAction()
                isAlreadyShown = false
                dialog?.dismiss()
            }
        }
        titleTextView.text =
            context?.resources?.getString(R.string.dear_user_we_have_found_suspicious_activity_in_order_to_continue_use_our_application_please_verify_yourself_and_slide_to_continue)

        isAlreadyShown = true
        shownAction()
    }

    override var cancelable: Boolean = false

    //  dialog view
    override val dialogView: View by lazy {
        LayoutInflater.from(context).inflate(R.layout.re_captcha_alert_dialog, null)
    }

    override val builder: AlertDialog.Builder = AlertDialog.Builder(context).setView(dialogView)

    //  offer image view
    private val alertDialogContentBackgroundConstraintLayout: ConstraintLayout by lazy {
        dialogView.findViewById(R.id.alertDialogContentBackgroundConstraintLayout)
    }

    //  offer image view
    private val logoImageView: ImageView by lazy {
        dialogView.findViewById(R.id.logoImageView)
    }

    //  title text view
    private val titleTextView: GivvyTextView by lazy {
        dialogView.findViewById(R.id.titleTextView)
    }

    //  okay button
    private val slider: SlideToActView by lazy {
        dialogView.findViewById(R.id.slider)
    }

    private fun placeViews(shuffledPosition: Int) {
        val root = alertDialogContentBackgroundConstraintLayout
        val constraintSet = ConstraintSet()
        constraintSet.clone(root)
        constraintSet.clear(logoImageView.id, ConstraintSet.TOP)
        constraintSet.clear(logoImageView.id, ConstraintSet.BOTTOM)
        constraintSet.clear(titleTextView.id, ConstraintSet.TOP)
        constraintSet.clear(titleTextView.id, ConstraintSet.BOTTOM)
        constraintSet.clear(slider.id, ConstraintSet.TOP)
        constraintSet.clear(slider.id, ConstraintSet.BOTTOM)

        when (shuffledPosition) {
            1 -> {
                constraintSet.connect(
                    logoImageView.id,
                    ConstraintSet.TOP,
                    alertDialogContentBackgroundConstraintLayout.id,
                    ConstraintSet.TOP,
                    30
                )
                constraintSet.connect(
                    slider.id,
                    ConstraintSet.TOP,
                    logoImageView.id,
                    ConstraintSet.BOTTOM,
                    30
                )
                constraintSet.connect(
                    titleTextView.id,
                    ConstraintSet.TOP,
                    slider.id,
                    ConstraintSet.BOTTOM,
                    30
                )
                constraintSet.connect(
                    titleTextView.id,
                    ConstraintSet.BOTTOM,
                    alertDialogContentBackgroundConstraintLayout.id,
                    ConstraintSet.BOTTOM,
                    80
                )
            }
            2 -> {
                constraintSet.connect(
                    titleTextView.id,
                    ConstraintSet.TOP,
                    alertDialogContentBackgroundConstraintLayout.id,
                    ConstraintSet.TOP,
                    50
                )
                constraintSet.connect(
                    logoImageView.id,
                    ConstraintSet.TOP,
                    titleTextView.id,
                    ConstraintSet.BOTTOM,
                    30
                )
                constraintSet.connect(
                    slider.id,
                    ConstraintSet.TOP,
                    logoImageView.id,
                    ConstraintSet.BOTTOM,
                    30
                )
                constraintSet.connect(
                    slider.id,
                    ConstraintSet.BOTTOM,
                    alertDialogContentBackgroundConstraintLayout.id,
                    ConstraintSet.BOTTOM,
                    80
                )
            }
            else -> {
                constraintSet.connect(
                    logoImageView.id,
                    ConstraintSet.TOP,
                    alertDialogContentBackgroundConstraintLayout.id,
                    ConstraintSet.TOP,
                    30
                )
                constraintSet.connect(
                    titleTextView.id,
                    ConstraintSet.TOP,
                    logoImageView.id,
                    ConstraintSet.BOTTOM,
                    30
                )
                constraintSet.connect(
                    slider.id,
                    ConstraintSet.TOP,
                    titleTextView.id,
                    ConstraintSet.BOTTOM,
                    60
                )
                constraintSet.connect(
                    slider.id,
                    ConstraintSet.BOTTOM,
                    alertDialogContentBackgroundConstraintLayout.id,
                    ConstraintSet.BOTTOM,
                    80
                )
            }
        }

        constraintSet.applyTo(root)
    }

}