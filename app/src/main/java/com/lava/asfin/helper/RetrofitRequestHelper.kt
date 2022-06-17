package com.lava.asfin.helper

import androidx.viewbinding.BuildConfig
import com.lava.asfin.util.Config.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

private var aspireClient: Retrofit? = null

class RetrofitRequestHelper {

    fun getAspireClient(): Retrofit {
        if (aspireClient == null) {
            val client = OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.MINUTES)
                .readTimeout(5, TimeUnit.MINUTES)
            if (BuildConfig.DEBUG) {
                val logging = HttpLoggingInterceptor()
                logging.level = HttpLoggingInterceptor.Level.BODY
                client.addInterceptor(logging)
            }
            aspireClient = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return aspireClient!!
    }
}