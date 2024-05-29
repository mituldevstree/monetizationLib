package com.monetizationlib.data.network.data

import android.accounts.NetworkErrorException
import android.util.MalformedJsonException
import com.google.gson.JsonSyntaxException
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import javax.net.ssl.HttpsURLConnection

object ResponseHandler {

    fun handleErrorResponse(error: Throwable): String {
        return when (error) {
            is SocketTimeoutException -> "Timeout, Might be Server not responding"
            is HttpException -> {
                when (error.code()) {
                    HttpsURLConnection.HTTP_UNAUTHORIZED -> "Unauthorised User"
                    HttpsURLConnection.HTTP_FORBIDDEN -> "Forbidden"
                    HttpsURLConnection.HTTP_INTERNAL_ERROR -> "Internal Server Error"
                    HttpsURLConnection.HTTP_BAD_REQUEST -> "Bad Request"
                    else -> error.getLocalizedMessage()
                }
            }

            is JsonSyntaxException, is MalformedJsonException -> "Our servers are experiencing downtime as part of maintenance"
            is NetworkErrorException, is IOException -> "Application requires network connection to perform operations. Please check your network connection and try again."
            else -> error.message.toString()
        }
    }

    fun getErrorMessage(code: Int): String {
        return when (code) {
            HttpsURLConnection.HTTP_UNAUTHORIZED -> "Unauthorised User"
            HttpsURLConnection.HTTP_FORBIDDEN -> "Forbidden"
            HttpsURLConnection.HTTP_INTERNAL_ERROR -> "Internal Server Error"
            HttpsURLConnection.HTTP_BAD_REQUEST -> "Bad Request"
            HttpsURLConnection.HTTP_UNAVAILABLE -> "Our servers are experiencing downtime as part of maintenance"
            else -> "Something Went Wrong"
        }
    }

    fun <T> handleNormalResponse(response: Response<T>, callback: ResponseCallback<T>?) {
        if (callback == null) return
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) callback.onSuccess(body)
            else callback.onError(response.message(), response.code())
        } else {
            val message = response.message().takeIf { response.code() == 200 }
                ?: ParserHelper.baseError(response.errorBody()).message
            callback.onError(message, response.code())
        }
    }

    fun <T> handleResponse(
        response: Response<ObjectBaseModel<T>>,
        callback: ResponseCallback<ObjectBaseModel<T>>?,
    ) {
        if (callback == null) return
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) callback.onSuccess(body)
            else callback.onError(response.message(), response.code())
        } else {
            val message = response.message().takeIf { response.code() == 200 }
                ?: ParserHelper.baseError(response.errorBody()).message
            callback.onError(message, response.code())
        }
    }

    /*fun <T> handleListResponse(
        response: Response<ListBaseModel<T>?>,
        callback: ResponseCallback<ListBaseModel<T>>?,
    ) {
        if (callback == null) return
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) callback.onSuccess(body)
            else callback.onError(response.message(), response.code())
        } else {
            val message = response.message().takeIf { response.code() == 200 }
                ?: ParserHelper.baseError(response.errorBody()).message
            callback.onError(message, response.code())
        }
    }*/
}