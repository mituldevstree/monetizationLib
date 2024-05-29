package com.monetizationlib.data.base.model.localLayer.persistance

import android.content.SharedPreferences

/**
 * The preferences class' purpose is to
 * create a default class which all local storage classes
 * can use as a default preferences.
 */
open class Preferences : PreferencesInternal() {
    override var instance: SharedPreferences? = this.preferences

    override val preferencesName: String
        get() = "com.givvy"
}

/**
 * The preferences internal class' purpose is to
 * create a base class from which all other shared preference
 * facades will inherit.
 */
sealed class PreferencesInternal {
    abstract val instance: SharedPreferences?

    abstract val preferencesName: String

    val preferences: SharedPreferences? = null
}