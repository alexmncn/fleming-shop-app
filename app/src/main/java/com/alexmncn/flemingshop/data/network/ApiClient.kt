package com.alexmncn.flemingshop.data.network

import android.content.Context
import okhttp3.OkHttpClient

object ApiClient {
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor { AuthManager.getToken() })
            .build()
    }
}