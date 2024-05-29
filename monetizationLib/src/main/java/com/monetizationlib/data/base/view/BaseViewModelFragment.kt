package com.monetizationlib.data.base.view

import android.app.AlertDialog
import android.content.Context
import android.graphics.drawable.Drawable
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.monetizationlib.data.R
import com.monetizationlib.data.base.model.networkLayer.layerSpecifics.ApiError
import com.monetizationlib.data.base.viewModel.BaseViewModel
import com.monetizationlib.data.base.viewModel.ResultAsyncState

/**ца
 * A Base Fragment class which works with a view model.
 */


abstract class BaseViewModelFragment<VM : BaseViewModel<*>,
        VDB : ViewDataBinding> : BaseFragment<VDB>() {

    protected abstract val viewModelClass: Class<VM>

    lateinit var viewModel: VM

    open var alertDialog: AlertDialog? = null

    open var loadingDialog: AlertDialog? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = ViewModelProviders.of(this).get(viewModelClass)
    }

    fun <T> newObserver(
        onSuccess: (T) -> Unit = {},
        onStart: () -> Unit = {},
        onFail: (ApiError) -> Unit = {},
        shouldUseDefaultErrorHandling: Boolean = true,
        withLoading: Boolean = true
    ): Observer<ResultAsyncState<T>> {
        return Observer {
            when (it) {
                is ResultAsyncState.Started -> {
                    when {
                        withLoading -> showLoading()
                    }
                    onStart()
                }
                is ResultAsyncState.Success -> {
                    hideLoading()
                    onSuccess(it.data)
                }
                is ResultAsyncState.Failed -> {
                    hideLoading()
                    if (shouldUseDefaultErrorHandling) {
                        if (it.apiError.statusCode == -1) {
                            showNeutralAlertDialog(it.apiError.userError ?: "Something went wrong.")
                        } else {
                            showNeutralAlertDialog(it.apiError.statusText ?: "Something went wrong.")
                        }
                    }
                    onFail(it.apiError)
                }
                else -> {
                    hideLoading()
                }
            }
        }
    }

    fun showNeutralAlertDialog(
        message: String,
        okayButtonTitle: String = resources.getString(R.string.okay),
        showSecondButton: Boolean = false,
        cancelButtonTitle: String = resources.getString(R.string.cancel),
        cancelable: Boolean = false,
        okayAction: (() -> Unit) = { alertDialog?.dismiss() },
        cancelAction: (() -> Unit) = { alertDialog?.dismiss() },
        drawable: Drawable? = null
    ): AlertDialog? {
        alertDialog?.dismiss()
        alertDialog = createNeutralAlert(
            message,
            okayButtonTitle,
            showSecondButton = showSecondButton,
            cancelButtonTitle = cancelButtonTitle,
            cancelable = cancelable,
            drawable = drawable
        ) {
            okayButtonClickListener {
                okayAction()
            }

            cancelButtonClickListener {
                cancelAction()
            }
        }
        alertDialog?.show()
        return alertDialog
    }

    inline fun Fragment.createNeutralAlert(
        message: String,
        okayButtonTitle: String = resources.getString(R.string.okay),
        showSecondButton: Boolean = false,
        cancelButtonTitle: String = resources.getString(R.string.cancel),
        cancelable: Boolean = false,
        drawable: Drawable? = null,
        func: NeutralAlertDialog.() -> Unit
    ): AlertDialog =
        NeutralAlertDialog(
            this.context,
            message,
            okayButtonTitle,
            showSecondButton = showSecondButton,
            cancelButtonTitle = cancelButtonTitle,
            cancelable = cancelable,
            drawable = drawable
        ).apply {
            func()
        }.create()


    private fun hideLoading() {
        loadingDialog?.dismiss()
        loadingDialog = null
    }

    private fun showLoading() {
        when (loadingDialog) {
            null -> {
                loadingDialog = LoadingDialog(context).create()
                loadingDialog?.show()
            }
        }
    }

}

