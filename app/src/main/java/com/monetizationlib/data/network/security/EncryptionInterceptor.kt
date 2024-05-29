package com.monetizationlib.data.network.security

import com.monetizationlib.data.network.ApiEndpoints
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody.Companion.toResponseBody
import org.json.JSONObject
import java.io.IOException

class EncryptionInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val rawBody = request.body
        var encryptedBody = rawBody.toString()
        val mediaType = "application/json".toMediaTypeOrNull()

        if (request.url.toString().contains(ApiEndpoints.VERSION_CHECK).not()) {
            try {
                encryptedBody = CryptoUtil.encryptRequestBodyToString(request)
                val json = JSONObject()
                json.put("verificationCode", encryptedBody)
                encryptedBody = "{\n \"verificationCode\":\"$encryptedBody\"\n}"
            } catch (e: Exception) {
                e.printStackTrace()
            }
            val toString = JSONObject(encryptedBody).toString()

            val body = toString.toRequestBody(mediaType)

            //build new request
            request = request.newBuilder()
                .method(request.method, body).headers(
                    request.headers
                ).build()
        }

        val response = chain.proceed(request)

        var responseString = response.body.string()

        if (responseString.contains("randomGenerationId")) {
            val jsonObject = JSONObject(responseString)
            responseString = try {
                AES.decrypt(jsonObject.getString("randomGenerationId"))
            } catch (throwable: Throwable) {
                ""
            }
        } else if (request.url.toString().equals("loginEstablished", ignoreCase = true)) {
            responseString = "{\n" +
                    "    “statusCode”: 400,\n" +
                    "    “statusDesc”: “missingId”,\n" +
                    "    “statusText”: “missingId” ,\n" +
                    "    “result”: {\n" +
                    "    }\n" +
                    "}"
        }

        val contentType: MediaType? = response.body.contentType()
        val body: ResponseBody = responseString.toResponseBody(contentType)

        return response.newBuilder().body(body).build()
    }
}