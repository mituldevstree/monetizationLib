package com.monetizationlib.data.base.view

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.monetizationlib.data.R

class LoadingDialog(context: Context?) : BaseDialogHelper() {

    override var cancelable: Boolean = false

    //  dialog view
    override val dialogView: View by lazy {
        LayoutInflater.from(context).inflate(R.layout.loading_dialog, null)
    }

    override val builder: AlertDialog.Builder =
        AlertDialog.Builder(context, android.R.style.Theme_Material_NoActionBar).setView(dialogView)
}