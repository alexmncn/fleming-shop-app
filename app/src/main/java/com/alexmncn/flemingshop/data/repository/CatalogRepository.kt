package com.alexmncn.flemingshop.data.repository

import com.alexmncn.flemingshop.data.model.Article
import com.alexmncn.flemingshop.data.model.Family
import com.alexmncn.flemingshop.data.network.ApiService
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken

class CatalogRepository(private val apiService: ApiService) {
    private val gson = Gson()

    // CATALOG func
    fun getAllArticles(page: Int = 1, perPage: Int = 20): List<Article> {
        val response = apiService.getAllArticles(page, perPage)
        if (response != null && response.isSuccessful) {
            val responseData = response.body?.string()
            val listType = object : TypeToken<List<Article>>() {}.type
            return gson.fromJson(responseData, listType)
        } else {
            throw Exception("Failed to fetch all articles")
        }
    }

    fun getAllArticlesTotal(): Int {
        val response = apiService.getAllArticlesTotal()
        if (response != null && response.isSuccessful) {
            val responseData = response.body?.string()
            return JsonParser.parseString(responseData).asJsonObject.get("total").asInt
        } else {
            throw Exception("Failed to fetch total number of articles")
        }
    }

    fun getSearchArticles(page: Int = 1, perPage: Int = 20, search: String, filter: String = "detalle"): List<Article> {
        val response = apiService.getSearchArticles(page, perPage, search, filter)
        if (response != null && response.isSuccessful) {
            val responseData = response.body?.string()
            val listType = object : TypeToken<List<Article>>() {}.type
            return gson.fromJson(responseData, listType)
        } else {
            throw Exception("Failed to search articles")
        }
    }

    fun getSearchArticlesTotal(search: String, filter: String = "detalle"): Int {
        val response = apiService.getSearchArticlesTotal(search, filter)
        if (response != null && response.isSuccessful) {
            val responseData = response.body?.string()
            return JsonParser.parseString(responseData).asJsonObject.get("total").asInt
        } else {
            throw Exception("Failed to fetch total number of search results")
        }
    }

    fun getFeaturedArticles(page: Int = 1, perPage: Int = 20): List<Article> {
        val response = apiService.getFeaturedArticles(page, perPage)
        if (response != null && response.isSuccessful) {
            val responseData = response.body?.string()
            val listType = object : TypeToken<List<Article>>() {}.type
            return gson.fromJson(responseData, listType)
        } else {
            throw Exception("Failed to fetch featured articles")
        }
    }

    fun getFeaturedArticlesTotal(): Int {
        val response = apiService.getFeaturedArticlesTotal()
        if (response != null && response.isSuccessful) {
            val responseData = response.body?.string()
            return JsonParser.parseString(responseData).asJsonObject.get("total").asInt
        } else {
            throw Exception("Failed to fetch total number of featured articles")
        }
    }

    fun getNewArticles(page: Int = 1, perPage: Int = 20): List<Article> {
        val response = apiService.getNewArticles(page, perPage)
        if (response != null && response.isSuccessful) {
            val responseData = response.body?.string()
            val listType = object : TypeToken<List<Article>>() {}.type
            return gson.fromJson(responseData, listType)
        } else {
            throw Exception("Failed to fetch new articles")
        }
    }

    fun getNewArticlesTotal(): Int {
        val response = apiService.getNewArticlesTotal()
        if (response != null && response.isSuccessful) {
            val responseData = response.body?.string()
            return JsonParser.parseString(responseData).asJsonObject.get("total").asInt
        } else {
            throw Exception("Failed to fetch total number of new articles")
        }
    }

    fun getFamilies(): List<Family> {
        val response = apiService.getFamilies()
        if (response != null && response.isSuccessful) {
            val responseData = response.body?.string()
            val listType = object : TypeToken<List<Family>>() {}.type
            return gson.fromJson(responseData, listType)
        } else {
            throw Exception("Failed to fetch families")
        }
    }

    fun getFamilyArticles(page: Int = 1, perPage: Int = 20, familyId: Int): List<Article> {
        val response = apiService.getFamilyArticles(page, perPage, familyId)
        if (response != null && response.isSuccessful) {
            val responseData = response.body?.string()
            val listType = object : TypeToken<List<Article>>() {}.type
            return gson.fromJson(responseData, listType)
        } else {
            throw Exception("Failed to fetch articles by family")
        }
    }

    fun getFamilyArticlesTotal(familyId: Int): Int {
        val response = apiService.getFamilyArticlesTotal(familyId)
        if (response != null && response.isSuccessful) {
            val responseData = response.body?.string()
            return JsonParser.parseString(responseData).asJsonObject.get("total").asInt
        } else {
            throw Exception("Failed to fetch total number of articles in family")
        }
    }
}

