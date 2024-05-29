package com.monetizationlib.data.base.model.networkLayer.networking

import com.monetizationlib.data.base.model.networkLayer.layerSpecifics.ApiResponse
import com.monetizationlib.data.base.model.networkLayer.layerSpecifics.toError
import com.monetizationlib.data.base.model.networkLayer.layerSpecifics.ApiError
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


abstract class DefaultCallback<T> : Callback<ApiResponse<T>> {

    override fun onResponse(call: Call<ApiResponse<T>>, response: Response<ApiResponse<T>>) {
        val body = response.body()
        when {
            response.isSuccessful && body != null -> {
                if (body.statusCode < 200 || body.statusCode > 300) {
                    onFail(body.toError())
                } else {
                    onSuccess(body.entity)
                }
            }

            else -> {
                when {
                    body != null -> {
                        onFail(body.toError())
                    }
                    else -> onFail(
                        ApiError(
                            response.code(),
                            
                            "No internet connection",
                            "No internet connection"
                        )
                    )
                }
            }
        }

        onCompleted()
    }

    override fun onFailure(call: Call<ApiResponse<T>>, t: Throwable) {
        val statusText = t.cause

        onFail(
            ApiError(
                -1,
                statusText.toString(),
                "No internet connection"
            )
        )

        onCompleted()
    }

    abstract fun onSuccess(response: T)

    protected open fun onFail(apiError: ApiError) {}

    protected open fun onCompleted() {}
}