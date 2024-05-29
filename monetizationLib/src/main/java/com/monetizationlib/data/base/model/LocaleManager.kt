package com.monetizationlib.data.base.model

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Build
import com.monetizationlib.data.base.model.PreferenceHelper.set
import org.json.JSONException
import org.json.JSONObject
import java.util.*


const val ENGLISH = "English"
const val BULGARIAN_SCREEN_NAME = "Български"
const val BULGARIAN = "Bulgarian"
const val FRENCH = "French"
const val SPANISH = "Spanish"
const val PORTUGUESE = "Portuguese"
const val INDONESIAN = "Indonesian"
const val CLICKS_COUNT = "clicks_count"
const val CLICKS_MISSED_COUNT = "clicks_missed_count"


object LocaleManager {

    private const val SELECTED_LANGUAGE = "userLanguage"
    private var mSharedPreference: SharedPreferences? = null
    private var clicksSharedPreferences: SharedPreferences? = null

    var mEnglishFlag = "en"
    var mBulgarianFlag = "bg"
    var mFrenchFlag = "fr"
    var mSpanishFlag = "es"
    var mIndonesianFlag = "in"
    var mPortugueseFlag = "pt"

    fun setLocale(context: Context?): Context {
        return updateResources(context!!, getCurrentLanguage(context)!!)
    }

    fun setNewLocale(context: Context, language: String) {
        persistLanguagePreference(context, language)
        updateResources(context, language)
    }


    fun getMadeClicks(context: Context, providerName: String = ""): Int {
        return getClicksSP(context = context)?.getInt(providerName + CLICKS_COUNT, 0) ?: 0
    }

    // Method to convert SharedPreferences content to JSON string
    fun sharedPreferencesToJson(context: Context): String {
        val sharedPreferences = PreferenceHelper.customPrefs(context, "clicks")
        val jsonObject = JSONObject()

        // Retrieve all key-value pairs from SharedPreferences
        val allEntries = sharedPreferences.all
        for ((key, value) in allEntries) {
            try {
                // Convert values to JSON format
                jsonObject.put(key, value)
            } catch (e: JSONException) {
                e.printStackTrace()
            }

        }

        // Convert JSON object to string
        return jsonObject.toString()
    }

    fun getSkippedClicks(context: Context, providerName: String = ""): Int {
        return getClicksSP(context = context)?.getInt(providerName + CLICKS_MISSED_COUNT, 0) ?: 0
    }

    fun setClickMade(context: Context, providerName: String = "") {
        val clicks = getMadeClicks(context, providerName)
        getClicksSP(context = context)?.edit()?.putInt(providerName + CLICKS_COUNT, clicks + 1)?.apply()
    }

    fun setClickSkipped(context: Context, providerName: String = "") {
        val missedClicks = getSkippedClicks(context, providerName)
        getClicksSP(context = context)?.edit()
            ?.putInt(providerName + CLICKS_MISSED_COUNT, missedClicks + 1)?.apply()
    }


    fun getCurrentLanguage(context: Context): String? {
        return getSP(context)?.getString(SELECTED_LANGUAGE, mEnglishFlag)
    }

    private fun persistLanguagePreference(context: Context, language: String) {
        getSP(context)?.set(SELECTED_LANGUAGE, language)
    }

    private fun getSP(context: Context): SharedPreferences? {
        if (mSharedPreference == null) {
            mSharedPreference = PreferenceHelper.defaultPrefs(context)
        }

        return mSharedPreference
    }

    private fun getClicksSP(context: Context): SharedPreferences? {
        if (clicksSharedPreferences == null) {
            clicksSharedPreferences = PreferenceHelper.customPrefs(context, "clicks")
        }

        return clicksSharedPreferences
    }


    private fun updateResources(context: Context, language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)

        val resources = context.resources
        val configuration = Configuration(resources.configuration)

        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> {
                configuration.setLocale(locale)
                configuration.setLayoutDirection(locale)

                context.createConfigurationContext(configuration)
            }

            else -> {
                configuration.locale = locale
                resources.updateConfiguration(configuration, resources.displayMetrics)
                context
            }
        }
    }

    fun getCurrentLanguageBackendHeaderName(context: Context): String {
        return when (getCurrentLanguage(context)?.toLowerCase()) {
            mBulgarianFlag -> BULGARIAN
            mEnglishFlag -> ENGLISH
            mFrenchFlag -> FRENCH
            mSpanishFlag -> SPANISH
            mPortugueseFlag -> PORTUGUESE
            mIndonesianFlag -> INDONESIAN
            else -> ENGLISH
        }
    }

}