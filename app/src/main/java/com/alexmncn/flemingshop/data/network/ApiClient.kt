package com.alexmncn.flemingshop.data.network

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

object ApiClient {
    private val client = OkHttpClient()

    @Throws(IOException::class)
    fun makeRequest(request: Request): Response {
        return client.newCall(request).execute()
    }
}