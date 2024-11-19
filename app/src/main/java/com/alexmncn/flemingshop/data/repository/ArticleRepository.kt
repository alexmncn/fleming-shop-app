package com.alexmncn.flemingshop.data.repository

import android.util.Log
import com.alexmncn.flemingshop.data.model.Article
import com.alexmncn.flemingshop.data.model.Family
import com.alexmncn.flemingshop.data.model.LoginResponse
import com.alexmncn.flemingshop.data.network.ApiService
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.gson.JsonParser

class ArticleRepository(private val apiService: ApiService) {

    private val gson = Gson()

    // CATALOG func
    suspend fun getAllArticles(page: Int = 1, perPage: Int = 20): List<Article> {
        val response = apiService.getAllArticles(page, perPage)
        if (response != null && response.isSuccessful) {
            val responseData = response.body?.string()
            val listType = object : TypeToken<List<Article>>() {}.type
            return gson.fromJson(responseData, listType)
        } else {
            throw Exception("Failed to fetch all articles")
        }
    }

    suspend fun getAllArticlesTotal(): Int {
        val response = apiService.getAllArticlesTotal()
        if (response != null && response.isSuccessful) {
            val responseData = response.body?.string()
            return JsonParser.parseString(responseData).asJsonObject.get("total").asInt
        } else {
            throw Exception("Failed to fetch total number of articles")
        }
    }

    suspend fun getSearchArticles(page: Int = 1, perPage: Int = 20, search: String, filter: String = "detalle"): List<Article> {
        val response = apiService.getSearchArticles(page, perPage, search, filter)
        if (response != null && response.isSuccessful) {
            val responseData = response.body?.string()
            val listType = object : TypeToken<List<Article>>() {}.type
            return gson.fromJson(responseData, listType)
        } else {
            throw Exception("Failed to search articles")
        }
    }

    suspend fun getSearchArticlesTotal(search: String, filter: String = "detalle"): Int {
        val response = apiService.getSearchArticlesTotal(search, filter)
        if (response != null && response.isSuccessful) {
            val responseData = response.body?.string()
            return JsonParser.parseString(responseData).asJsonObject.get("total").asInt
        } else {
            throw Exception("Failed to fetch total number of search results")
        }
    }

    suspend fun getFeaturedArticles(page: Int = 1, perPage: Int = 20): List<Article> {
        val response = apiService.getFeaturedArticles(page, perPage)
        if (response != null && response.isSuccessful) {
            val responseData = response.body?.string()
            val listType = object : TypeToken<List<Article>>() {}.type
            return gson.fromJson(responseData, listType)
        } else {
            throw Exception("Failed to fetch featured articles")
        }
    }

    suspend fun getFeaturedArticlesTotal(): Int {
        val response = apiService.getFeaturedArticlesTotal()
        if (response != null && response.isSuccessful) {
            val responseData = response.body?.string()
            Log.d("articles", responseData.toString())
            return JsonParser.parseString(responseData).asJsonObject.get("total").asInt
        } else {
            throw Exception("Failed to fetch total number of featured articles")
        }
    }

    suspend fun getNewArticles(page: Int = 1, perPage: Int = 20): List<Article> {
        val response = apiService.getNewArticles(page, perPage)
        if (response != null && response.isSuccessful) {
            val responseData = response.body?.string()
            val listType = object : TypeToken<List<Article>>() {}.type
            return gson.fromJson(responseData, listType)
        } else {
            throw Exception("Failed to fetch new articles")
        }
    }

    suspend fun getNewArticlesTotal(): Int {
        val response = apiService.getNewArticlesTotal()
        if (response != null && response.isSuccessful) {
            val responseData = response.body?.string()
            return JsonParser.parseString(responseData).asJsonObject.get("total").asInt
        } else {
            throw Exception("Failed to fetch total number of new articles")
        }
    }

    suspend fun getFamilies(): List<Family> {
        val response = apiService.getFamilies()
        if (response != null && response.isSuccessful) {
            val responseData = response.body?.string()
            val listType = object : TypeToken<List<Family>>() {}.type
            return gson.fromJson(responseData, listType)
        } else {
            throw Exception("Failed to fetch families")
        }
    }

    suspend fun getFamilyArticles(page: Int = 1, perPage: Int = 20, familyId: Int): List<Article> {
        val response = apiService.getFamilyArticles(page, perPage, familyId)
        if (response != null && response.isSuccessful) {
            val responseData = response.body?.string()
            val listType = object : TypeToken<List<Article>>() {}.type
            return gson.fromJson(responseData, listType)
        } else {
            throw Exception("Failed to fetch articles by family")
        }
    }

    suspend fun getFamilyArticlesTotal(familyId: Int): Int {
        val response = apiService.getFamilyArticlesTotal(familyId)
        if (response != null && response.isSuccessful) {
            val responseData = response.body?.string()
            return JsonParser.parseString(responseData).asJsonObject.get("total").asInt
        } else {
            throw Exception("Failed to fetch total number of articles in family")
        }
    }


    // AUTH func
    suspend fun login(username: String, password: String): LoginResponse {
        val response = apiService.login(username, password)
        if (response != null && response.isSuccessful) {
            val responseData = response.body?.string()
            return gson.fromJson(responseData, LoginResponse::class.java)
        } else if (response?.code == 401) {
            throw Exception("Unauthorized: Incorrect username or password")
        } else {
            throw Exception("Failed to login: ${response?.message}")
        }
    }

    suspend fun logout(): String? {
        val response = apiService.logout()
        if (response != null && response.isSuccessful) {
            val responseData = response.body?.string()
            return responseData
        } else {
            throw Exception("Failed to logout: ${response?.message}")
        }
    }
}

