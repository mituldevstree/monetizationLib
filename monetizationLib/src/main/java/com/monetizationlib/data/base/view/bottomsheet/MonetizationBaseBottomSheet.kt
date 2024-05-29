package com.monetizationlib.data.base.view.bottomsheet

import android.app.Dialog
import android.content.DialogInterface
import android.content.DialogInterface.OnShowListener
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.fragment.app.FragmentActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.monetizationlib.data.R
import com.monetizationlib.data.base.extensions.justTry

abstract class MonetizationBaseBottomSheet : BottomSheetDialogFragment(), OnShowListener {
    private var expanded = true
    private var showKeyboard = false
    private var isApplyStyle = true
    private var bottomSheetBehavior: BottomSheetBehavior<*>? = null

    private val bottomSheetCallback: BottomSheetCallback = object : BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) dismiss()
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (isApplyStyle) setStyle(STYLE_NORMAL, R.style.OfferBottomSheetDialogTheme)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.let {
            if (showKeyboard) it.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
            it.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_MODE_CHANGED)
        }
        return setExpanded(dialog)!!
    }

    fun setExpanded(expanded: Boolean) {
        this.expanded = expanded
    }

    fun setKeyboard(isVisible: Boolean) {
        showKeyboard = isVisible
    }

    private fun setExpanded(dialog: Dialog?): Dialog? {
        if (dialog == null) return null
        dialog.window?.let {
            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            if (expanded) {
                it.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            }
        }

        dialog.setOnShowListener(this)
        return dialog
    }

    fun hideBottomSheet() {
        justTry {
            if (bottomSheetBehavior == null) return
            dismiss()
        }
    }

    override fun onShow(dialog: DialogInterface) {
        val bottomSheetDialog = dialog as BottomSheetDialog
        val bottomSheet = bottomSheetDialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
        if (bottomSheet != null) {
            bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
            bottomSheetBehavior?.addBottomSheetCallback(bottomSheetCallback)
            val layoutParams = bottomSheet.layoutParams
            if (!expanded) return
            bottomSheetBehavior?.peekHeight = Resources.getSystem().displayMetrics.heightPixels
            if (layoutParams != null) layoutParams.height = getWindowHeight()
            bottomSheet.layoutParams = layoutParams
            bottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
        }
        bottomSheetDialog.setOnDismissListener {
            dismissAllowingStateLoss()
//            hideKeyBoard()
        }
        bottomSheetDialog.setOnCancelListener {
            dismissAllowingStateLoss()
        }
    }

    private fun getWindowHeight(): Int {
        // Calculate window height for fullscreen use
        val displayMetrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val dialog = dialog ?: return
        val window = dialog.window ?: return
        window.callback = MonetizationUserInteractionAwareCallback(window.callback, activity)
    }

    open fun show(activity: FragmentActivity): MonetizationBaseBottomSheet? {
        return show(activity, TAG)
    }

    fun show(activity: FragmentActivity, tag: String?): MonetizationBaseBottomSheet? {
        return if (activity.isFinishing || activity.isDestroyed) null else try {
            val fragmentManager = activity.supportFragmentManager
            val fragment = fragmentManager.findFragmentByTag(tag)
            if (fragment != null) return null
            showNow(activity.supportFragmentManager, tag)
            this
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    companion object {
        private val TAG = MonetizationBaseBottomSheet::class.java.simpleName
    }
}