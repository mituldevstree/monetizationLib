package com.monetizationlib.data.base.viewModel

import com.monetizationlib.data.base.model.networkLayer.layerSpecifics.ApiError

/**
 * The class is a wrapper for every live data entity
 * which will be passed through the modules (V, VM, BM, M).
 *
 * It will give the client class information about the state
 * of the data.
 */
sealed class ResultAsyncState<T> {
    class Started<T> : ResultAsyncState<T>()
    class InProgress<Double>(var progress: Double) : ResultAsyncState<Double>()
    class Success<T>(var data: T) : ResultAsyncState<T>()
    class Failed<T>(val apiError: ApiError) : ResultAsyncState<T>()
}