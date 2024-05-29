package com.monetizationlib.data.application

import android.app.Application

class Controller : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        //AppLovinSdk.getInstance(this).showMediationDebugger()
    }

    companion object {
        lateinit var instance: Controller
        var wasMonetizationInitialized: Boolean = false
    }
}