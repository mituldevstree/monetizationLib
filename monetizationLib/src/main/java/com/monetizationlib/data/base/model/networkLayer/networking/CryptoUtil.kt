package com.monetizationlib.data.base.model.networkLayer.networking

import okhttp3.Request
import okio.Buffer
import java.io.IOException

object CryptoUtil {

    fun encryptRequestBodyToString(request: Request): String {
        val rawBodyString = bodyToString(request)

        var encryptedString = AES.encode(rawBodyString ?: "{}", "daumirathakerite")

        if (rawBodyString.isNullOrEmpty()) {
            encryptedString = AES.encode("{}", "daumirathakerite")
        } else {
            encryptedString = AES.encode(rawBodyString, "daumirathakerite")
        }

        return encryptedString ?: ""
    }

    private fun bodyToString(request: Request): String? {
        return try {
            val copy: Request = request.newBuilder().build()
            val buffer = Buffer()
            copy.body?.writeTo(buffer)
            buffer.readUtf8()
        } catch (e: IOException) {
            "{}"
        }
    }

    fun encode(s: String, k: Int): String {
        var index = k
        var shifted = ""

        for (element in s) {
            val `val` = element.toInt()
            shifted += (`val` + index).toChar()
        }

        return shifted
    }
}
