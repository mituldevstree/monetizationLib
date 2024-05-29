package com.monetizationlib.data.network.client

import android.util.Log
import com.example.givvymonetization.BuildConfig
import com.monetizationlib.data.localcache.LocalDataHelper

import com.monetizationlib.data.network.ApiEndpoints
import com.monetizationlib.data.network.security.EncryptionInterceptor
import com.monetizationlib.data.network.domain.GeneralApiService
import com.monetizationlib.data.base.extensions.justTry
import okhttp3.ConnectionSpec
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit


object RetroClient {
    private lateinit var retrofit: Retrofit
    private val requestLogInterceptor: Interceptor
        get() {
            val logging = HttpLoggingInterceptor { }
            logging.setLevel(HttpLoggingInterceptor.Level.BODY.takeIf { BuildConfig.DEBUG }
                ?: HttpLoggingInterceptor.Level.NONE)
            return logging
        }

    private val headerLogInterceptor: Interceptor
        get() {
            val logging = HttpLoggingInterceptor { message -> Log.e("API", message) }
            logging.setLevel(HttpLoggingInterceptor.Level.BODY.takeIf { BuildConfig.DEBUG }
                ?: HttpLoggingInterceptor.Level.NONE)
            return logging
        }

    private val builder = OkHttpClient.Builder()
        .connectionSpecs(listOf(ConnectionSpec.CLEARTEXT, ConnectionSpec.MODERN_TLS))
        .connectTimeout(80, TimeUnit.SECONDS).writeTimeout(80, TimeUnit.SECONDS)
        .readTimeout(80, TimeUnit.SECONDS)

    private val headerInterceptor = Interceptor { chain ->
        val request = chain.request().newBuilder()
        val lang = "English"
        request.addHeader(ApiEndpoints.HEADER_CURRENCY, "USD")
        request.addHeader("Connection", "close")
        request.addHeader(ApiEndpoints.HEADER_LANGUAGE, lang)
        request.addHeader("isproduction","true")
        request.addHeader(ApiEndpoints.HEADER_VERSION,BuildConfig.VERSION_NAME)
        request.addHeader(ApiEndpoints.HEADER_PACKAGE_NAME, BuildConfig.APPLICATION_ID)
        if (LocalDataHelper.getUserDetail() != null) {
            justTry {
                request.addHeader(ApiEndpoints.HEADER_SESSION, LocalDataHelper.getAuthCode())
            }
        }
        chain.proceed(request.build())
    }

    private val forbiddenInterceptor = Interceptor { chain ->
        val request = chain.request()
        val response = chain.proceed(request)
        response
    }
    private val encryptInterceptor by lazy {
        EncryptionInterceptor()
    }



    private val client =
        builder.addNetworkInterceptor(headerLogInterceptor)
            .addInterceptor(requestLogInterceptor)
            .addInterceptor(headerInterceptor)
            .addInterceptor(encryptInterceptor)
            .addInterceptor(forbiddenInterceptor).build()

    private val retrofitInstance: Retrofit
        get() {
            if (RetroClient::retrofit.isInitialized.not()) retrofit =
                Retrofit.Builder().baseUrl(/*"https://givvy-crypto-miner-30dd537816d2.herokuapp.com/"*/
                "https://givvy-video-backend.herokuapp.com/"
             /*   "https://givvy-lock-screen-b4b2fd33f346.herokuapp.com/"*/
                ).client(client)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create()).build()
            return retrofit
        }


    val GENERAL_API_SERVICE: GeneralApiService =
        retrofitInstance.create(GeneralApiService::class.java)



}