package com.monetizationlib.data.base.model.networkLayer.networking

import android.content.Context
import android.util.Log
import com.monetizationlib.data.BuildConfig
import com.monetizationlib.data.Monetization
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * The network manager is a class
 * encapsulating all base logic around the building of APIs
 * and initializing networking libraries.
 */
object NetworkManager {
    lateinit var retrofit: Retrofit

    var retrofitOfferwall: Retrofit? = null

    var retrofitOuter: Retrofit? = null

    lateinit var defaultApiService: CommonService

    var clientPersisted: OkHttpClient? = null

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

    fun initialize(
        context: Context,
        lang: String,
        versionName: String,
        packageName: String,
        isProductionEnabled: Boolean
    ) {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val languageInterceptor = Interceptor { chain ->
            val newBuilder = chain.request().newBuilder()

            newBuilder.addHeader(
                "language",
                lang
            )

            newBuilder.addHeader(
                "versionName",
                versionName
            )

            newBuilder.addHeader(
                "isProduction",
                isProductionEnabled.toString()
            )

            newBuilder.addHeader(
                "packageName",
                packageName
            )

            val request: Request = newBuilder.build()

            chain.proceed(request)
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(languageInterceptor)
            .addInterceptor(requestLogInterceptor)
            .addInterceptor(headerLogInterceptor)
            .addInterceptor(EncryptionInterceptor())
            .addInterceptor(interceptor)
            .build()

        val clientOuter = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()

        clientPersisted = client

        retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.API_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofitOfferwall = Retrofit.Builder()
            .baseUrl("https://givvy-offerwall-sdk.herokuapp.com")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofitOuter = Retrofit.Builder()
            .baseUrl("https://freeipapi.com")
            .client(clientOuter)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        defaultApiService = retrofit.create(CommonService::class.java)
    }

    fun reinitRetrofitOfferwall() {
        retrofitOfferwall = clientPersisted?.let {
            Retrofit.Builder()
                .baseUrl("https://givvy-offerwall-sdk.herokuapp.com")
                .client(it)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
    }
}