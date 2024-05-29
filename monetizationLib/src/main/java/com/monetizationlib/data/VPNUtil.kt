package com.monetizationlib.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build


object VPNUtil {
    fun isUsingVpn(context: Context): Boolean {
        //checkVPNStatus(context)
        val cm =
            context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networks: Array<Network> = cm.allNetworks

        for (i in networks.indices) {
            val caps = cm.getNetworkCapabilities(networks[i])
            if (caps?.hasTransport(NetworkCapabilities.TRANSPORT_VPN) == true) {
                return true
            }
        }

        return false
    }


    private fun checkVPNStatus(context: Context): Boolean {
        val connectivityManager = context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork
            val capabilities = connectivityManager.getNetworkCapabilities(network)
            android.util.Log.wtf("LOG_WTF", "in New Android Version == ${capabilities!= null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)}")
            capabilities!= null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)
        } else {
            android.util.Log.wtf("LOG_WTF", "in Old Android Version == ${connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_VPN)!!.isConnectedOrConnecting}")
            connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_VPN)!!.isConnectedOrConnecting
        }
    }


    fun isEmulator(): Boolean {
        if (Monetization.monetizationConfig?.shouldCheckForEmulator == false) {
            return false
        }

        if (Monetization.monetizationConfig?.shouldBlockOffersForUser == true) {
            return true
        }

        return (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")
                || Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.HARDWARE.contains("goldfish")
                || Build.HARDWARE.contains("ranchu")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || Build.PRODUCT.contains("sdk_google")
                || Build.PRODUCT.contains("google_sdk")
                || Build.PRODUCT.contains("sdk")
                || Build.PRODUCT.contains("sdk_x86")
                || Build.PRODUCT.contains("sdk_gphone64_arm64")
                || Build.PRODUCT.contains("vbox86p")
                || Build.PRODUCT.contains("emulator")
                || Build.PRODUCT.contains("simulator"))
    }
}