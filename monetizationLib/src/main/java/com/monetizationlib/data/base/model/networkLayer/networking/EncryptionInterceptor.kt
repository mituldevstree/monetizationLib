package com.monetizationlib.data.base.model.networkLayer.networking

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException

class EncryptionInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val rawBody = request.body
        var encryptedBody = rawBody.toString()

        val mediaType = "application/json".toMediaTypeOrNull()

        if (request.url.toString().contains("getStatus")
            || request.url.toString().contains("getChatStatus")
            || request.url.toString().contains("Foreground")
            || request.url.toString().contains("markUserAsReal")
            || request.url.toString().contains("checkIfEligibleForAdditionalReward")
            || request.url.toString().contains("getFastReward")
            || request.url.toString().contains("saveFastWithdrawData")
            || request.url.toString().contains("markUserAsReal")
            || request.url.toString().contains("giveRewardForDownloadedApp")
            || request.url.toString().contains("newAppIsDownloaded")
            || request.url.toString().contains("sendErrorMessage")
        ) {
            try {
                encryptedBody = CryptoUtil.encryptRequestBodyToString(request)

                val json = JSONObject()

                json.put("verificationCode", encryptedBody)

                encryptedBody = "{\n \"verificationCode\":\"$encryptedBody\"\n}"
            } catch (e: Exception) {
                e.printStackTrace()
            }
            val toString = JSONObject(encryptedBody).toString()

            val body = RequestBody.create(mediaType, toString)

            //build new request
            request = request.newBuilder()
                .method(request.method, body).headers(
                    request.headers
                ).build()
        }

        val response = chain.proceed(request)

        var responseString = response.body.string()

        if (responseString?.contains("randomGenerationId") == true) {
            val jsonObject = JSONObject(responseString)
            responseString = try {
                AES.decrypt(jsonObject.getString("randomGenerationId")).toString()
            } catch (throwable: Throwable) {
                ""
            }
        } else if (request.url.toString().equals("loginEstablished")) {
            responseString = "{\n" +
                    "    “statusCode”: 400,\n" +
                    "    “statusDesc”: “missingId”,\n" +
                    "    “statusText”: “missingId” ,\n" +
                    "    “result”: {\n" +
                    "    }\n" +
                    "}"
        }

        val contentType: MediaType? = response.body.contentType()

        val body: ResponseBody = responseString.let { ResponseBody.create(contentType, it) }

        return response.newBuilder().body(body).build()
    }
}