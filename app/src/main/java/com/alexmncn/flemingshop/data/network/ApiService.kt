package com.alexmncn.flemingshop.data.network

import com.alexmncn.flemingshop.utils.Constans
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.io.IOException
import java.math.BigInteger

object ApiService {
    // Funcion HTTP GET que recibe la ruta, hace la peticion y devuelve la respuesta
    private fun  makeGetRequest(route: String): Response? {
        return try {
            val request = Request.Builder()
                .get()
                .url(Constans.BASE_URL+route)
                .build()

            ApiClient.makeRequest(request)
        } catch (e: IOException) {
            null
        }
    }

    private fun  makePostRequest(route: String, body: RequestBody): Response? {
        return try {
            val request = Request.Builder()
                .post(body)
                .url(Constans.BASE_URL+route)
                .build()

            ApiClient.makeRequest(request)
        } catch (e: IOException) {
            null
        }
    }

    // API ENDPOINTS

    // Catalog routes
    fun getAllArticlesTotal(): Response? {
        val route = "articles/total"
        return makeGetRequest(route)
    }

    fun getAllArticles(page: Int = 1, perPage: Int = 20): Response? {
        val route = "articles/all?page=$page&per_page=$perPage"
        return makeGetRequest(route)
    }

    fun getSearchArticlesTotal(search: String): Response? {
        val route = "articles/search/total?search=$search"
        return makeGetRequest(route)
    }

    fun getSearchArticles(page: Int = 1, perPage: Int = 20, search: String): Response? {
        val route = "articles/search?page=$page&per_page=$perPage&search=$search"
        return makeGetRequest(route)
    }

    fun getFeaturedArticlesTotal(): Response? {
        val route = "articles/featured/total"
        return makeGetRequest(route)
    }

    fun getFeaturedArticles(page: Int = 1, perPage: Int = 20): Response? {
        val route = "articles/featured?page=$page&per_page=$perPage"
        return makeGetRequest(route)
    }

    fun getNewArticlesTotal(): Response? {
        val route = "articles/new/total"
        return makeGetRequest(route)
    }

    fun getNewArticles(page: Int = 1, perPage: Int = 20): Response? {
        val route = "articles/new?page=$page&per_page=$perPage"
        return makeGetRequest(route)
    }

    fun getFamilies(): Response? {
        val route = "articles/families"
        return  makeGetRequest(route)
    }

    fun getFamilyArticlesTotal(familyId: Int): Response? {
        val route = "articles/families/$familyId/total"
        return makeGetRequest(route)
    }

    fun getFamilyArticles(page: Int = 1, perPage: Int = 20, familyId: Int): Response? {
        val route = "articles/families/$familyId?page=$page&per_page=$perPage"
        return makeGetRequest(route)
    }
}
