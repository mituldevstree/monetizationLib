package com.monetizationlib.data.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.givvymonetization.BuildConfig
import com.monetizationlib.data.application.Controller
import com.monetizationlib.data.localcache.LocalDataHelper
import com.monetizationlib.data.network.ApiEndpoints.RESPONSE_OK
import com.monetizationlib.data.network.domain.GeneralRepository
import com.monetizationlib.data.network.security.TimeStampManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.util.Locale
import java.util.UUID

class UserViewModel() : ViewModel() {

//    private val _state = StateReducerFlow(
//        initialState = UserState.defaultValue,
//        reduceState = ::reduceState,
//    )


    /**
     * Version check configs.
     */
     fun versionCheck(onError: () -> Unit, onProgressComplete: () -> Unit) {
        val params: MutableMap<String, Any> = HashMap()
        params["version"] = BuildConfig.VERSION_NAME
        viewModelScope.launch {
            delay(400)
            GeneralRepository.versionCheck(params).catch { result ->
                onError.invoke()
            }.collect { result ->
                if (result != null && result.code == RESPONSE_OK) {
                    LocalDataHelper.setAppConfig(result.data)
                    result.data?.date?.let { date ->
                        TimeStampManager.backendTimeStamp = date
                        TimeStampManager.completedTimeStamp = System.currentTimeMillis()
                    }
                    registerDevice(onError = onError, onSuccess = onProgressComplete)
                } else {
                    onError.invoke()
                }
            }
        }

    }


    /**
     * Device registration and generating user for that device.
     */
    private fun registerDevice(onError: () -> Unit, onSuccess: () -> Unit) {
        val params: MutableMap<String, Any> = HashMap()
        params["deviceId"] = getUniquePsuedoID()
        params["deviceType"] = "Android"
        val user = LocalDataHelper.getUserDetail()
        if (user != null && user.externalUserID.isNullOrEmpty().not()) {
            params["externalId"] = user.externalUserID.toString()
        }
        if (user != null && user.externalSessionCode.isNullOrEmpty().not()) {
            params["userId"] = user.id ?: ""
            params["rqCode"] = user.externalSessionCode ?: ""
        }
        viewModelScope.launch {
            GeneralRepository.registerDevice(params, Dispatchers.IO).collect {
                if (it != null && it.code == RESPONSE_OK) {
                    val data = it.data
                    LocalDataHelper.setSessionId(data?.sessionId)
                    LocalDataHelper.setUserConfig(data?.userConfig)
                    LocalDataHelper.setUserDetail(data?.user)
                    onSuccess.invoke()
                } else {
                    onError.invoke()
                }
            }
        }
    }


    fun getUniquePsuedoID(): String {
        val identifier = generateDeviceIdentifier()
        if (identifier.isNotEmpty()) return identifier

        val uniqueSerialCode = "35"
        +Build.BOARD.length % 10
        +Build.BRAND.length % 10
        +Build.SUPPORTED_ABIS[0].length % 10
        +Build.DEVICE.length % 10
        +Build.MANUFACTURER.length % 10
        +Build.MODEL.length % 10
        +Build.PRODUCT.length % 10

        var serial: String?
        try {
            serial = Build::class.java.getField("SERIAL")[null]!!.toString()
            return UUID(
                uniqueSerialCode.hashCode().toLong(), serial.hashCode().toLong()
            ).toString()
        } catch (exception: Exception) {
            serial = "serial"
        }
        return UUID(uniqueSerialCode.hashCode().toLong(), serial.hashCode().toLong()).toString()
    }

    @SuppressLint("HardwareIds")
    private fun generateDeviceIdentifier(context: Context = Controller.instance): String {
        val pseudoId = "35"
        +Build.BOARD.length % 10
        +Build.BRAND.length % 10
        +Build.SUPPORTED_ABIS[0].length % 10
        +Build.DEVICE.length % 10
        +Build.DISPLAY.length % 10
        +Build.HOST.length % 10
        +Build.ID.length % 10
        +Build.MANUFACTURER.length % 10
        +Build.MODEL.length % 10
        +Build.PRODUCT.length % 10
        +Build.TAGS.length % 10
        +Build.TYPE.length % 10
        +Build.USER.length % 10

        val androidId =
            Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        val longId = pseudoId + androidId
        try {
            val messageDigest = MessageDigest.getInstance("MD5")
            messageDigest.update(longId.toByteArray(), 0, longId.length)
            val md5Bytes: ByteArray = messageDigest.digest()
            var identifier = ""
            for (md5Byte in md5Bytes) {
                val b = 0xFF and md5Byte.toInt()
                if (b <= 0xF) {
                    identifier += "0"
                }
                identifier += Integer.toHexString(b)
            }
            return identifier.uppercase(Locale.ENGLISH)
        } catch (e: java.lang.Exception) {
            Log.e("TAG", e.toString())
        }
        return ""
    }

}
