package com.monetizationlib.data.network.security

import android.util.Log
import com.google.gson.JsonParser
import com.monetizationlib.data.base.extensions.toJson
import okhttp3.Request
import okio.Buffer
import java.io.IOException

object CryptoUtil {

    fun encryptRequestBodyToString(request: Request): String {
        var rawBodyString = bodyToString(request)

        try {
            val jsonParser = JsonParser()
            val jsonObject = jsonParser.parse(rawBodyString).asJsonObject
            Log.e("API Param", jsonObject.toJson())
            val verts = TimeStampManager.getTimeStampDiff()
            jsonObject.addProperty("verts", verts)
            rawBodyString = jsonObject.toString()
        } catch (_: Throwable) {

        }

        val encryptedString = if (rawBodyString.isNullOrEmpty()) {
            AES.encode("{}", /*"appWorldKey")*/"ressty")
        } else {
            AES.encode(rawBodyString, /*"appWorldKey")*/"ressty")
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
