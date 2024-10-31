package com.alexmncn.flemingshop.data.network

import com.alexmncn.flemingshop.utils.Constans
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

object ApiService {
    private fun mkRequest(request: Request): Response? {
        return try {
            ApiClient.makeRequest(request)
        } catch (e: IOException) {
            null
        }
    }

    // API endpoints

    fun getAllArticlesTotal(): Response? {
        val request = Request.Builder()
            .url(Constans.BASE_URL+"articles/total")
            .build()

        return mkRequest(request)
    }

    fun getAllArticles(page: Int = 1, perPage: Int = 20): Response? {
        val request = Request.Builder()
            .url(Constans.BASE_URL+"articles/all?page=$page&per_page=$perPage")
            .build()

        return mkRequest(request)
    }
}
