package dev.muthukumar.anjana_crm.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    // Change to your backend IP when testing on a real device
    // For emulator: 10.0.2.2  |  For real device: your local IP e.g. 192.168.1.5
    private const val BASE_URL = "http://127.0.0.1:8080/api/"

//    private const val BASE_URL = "https://office-crm-backend.onrender.com/api/"

    private var tokenProvider: (() -> String?) = { null }

    fun init(provider: () -> String?) {
        tokenProvider = provider
    }

    private val okHttp by lazy {
        OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(tokenProvider))
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val service: ApiService by lazy { retrofit.create(ApiService::class.java) }
}
