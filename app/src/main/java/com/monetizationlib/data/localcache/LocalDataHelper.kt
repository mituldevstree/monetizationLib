package com.monetizationlib.data.localcache


import com.monetizationlib.data.localcache.PreferenceManager.Companion.getInstance
import com.monetizationlib.data.localcache.SharedPrefConstant.APP_CONFIG
import com.monetizationlib.data.localcache.SharedPrefConstant.SESSION_ID
import com.monetizationlib.data.localcache.SharedPrefConstant.USER_CONFIG
import com.monetizationlib.data.localcache.SharedPrefConstant.USER_DETAILS
import com.monetizationlib.data.network.model.AppConfig
import com.monetizationlib.data.network.model.User
import com.monetizationlib.data.network.model.UserConfig
import com.google.gson.Gson
import com.monetizationlib.data.base.extensions.ViewUtil

object LocalDataHelper {

    private var user: User? = null


    fun setSessionId(token: String?) {
        token ?: return
        return getInstance().putString(SESSION_ID, token)
    }

    fun getSessionId(): String? {
        return getInstance().getString(SESSION_ID)
    }

    fun getAuthCode(): String {
        var authCode = ""
        getSessionId()?.let { id ->
            if (id.isNotEmpty()) authCode =
                authCode.plus(ViewUtil.sessionIdEncode(id.substring(3, 8), 4))
        }

        getUserDetail()?.id?.let { id ->
            if (id.isNotEmpty()) authCode =
                authCode.plus(ViewUtil.sessionIdEncode(id.substring(5, 11), 5))
        }
        return authCode
    }



    fun setUserDetail(updatedUser: User?) {
        user = updatedUser
        user?.notifyChange()
        getInstance().putString(USER_DETAILS, Gson().toJson(updatedUser))
    }

    fun getUserDetail(): User? {
        if (user == null) {
            user = Gson().fromJson(getInstance().getString(USER_DETAILS), User::class.java)
        }
        return user
    }

    fun setUserConfig(user: UserConfig?) {
        getInstance().putString(USER_CONFIG, Gson().toJson(user))
    }

    fun getUserConfig(): UserConfig? {
        return Gson().fromJson(getInstance().getString(USER_CONFIG), UserConfig::class.java)
    }

    fun setAppConfig(appConfig: AppConfig?) {
        if (appConfig == null) return
        getInstance().putString(APP_CONFIG, Gson().toJson(appConfig))
    }

    fun getAppConfig(): AppConfig? {
        return Gson().fromJson(getInstance().getString(APP_CONFIG), AppConfig::class.java)
    }


}