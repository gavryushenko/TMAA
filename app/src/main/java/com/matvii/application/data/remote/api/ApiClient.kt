package com.matvii.application.data.remote.api

import com.matvii.application.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "https://api.openweathermap.org/"
    private const val API_KEY_PARAM = "appid"

    // Jednoduchý interceptor: přidá API klíč do každého požadavku automaticky.
    private val apiKeyInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        val apiKey = BuildConfig.OPENWEATHER_API_KEY

        val newUrl = originalRequest.url().newBuilder().apply {
            if (apiKey.isNotBlank()) addQueryParameter(API_KEY_PARAM, apiKey)
        }.build()

        val newRequest = originalRequest.newBuilder()
            .url(newUrl)
            .build()

        chain.proceed(newRequest)
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(apiKeyInterceptor)
            .build()
    }

    val weatherApi: WeatherApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApiService::class.java)
    }
}
