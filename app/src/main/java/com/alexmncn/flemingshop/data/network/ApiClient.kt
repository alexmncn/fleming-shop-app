package com.alexmncn.flemingshop.data.network

import android.content.Context
import okhttp3.OkHttpClient

object ApiClient {
    fun provideOkHttpClient(context: Context): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(context))
            .build()
    }
}