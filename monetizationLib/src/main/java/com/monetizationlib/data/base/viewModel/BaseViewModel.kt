package com.monetizationlib.data.base.viewModel

import androidx.lifecycle.ViewModel
import com.monetizationlib.data.base.businessModule.BusinessModule

abstract class BaseViewModel<T> : ViewModel() where T : BusinessModule<*> {
    abstract val businessModule: T
}