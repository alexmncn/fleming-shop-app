package com.alexmncn.flemingshop.data.repository

import com.alexmncn.flemingshop.data.model.Article
import com.alexmncn.flemingshop.data.network.ApiService

class ArticleRepository(private val apiService: ApiService) {
    suspend fun getAllArticlesTotal(): Int {
        return apiService.getAllArticlesTotal()
    }

    suspend fun getAllArticles(page: Int = 1, perPage: Int = 20): List<Article> {
        return apiService.getAllArticles(page, perPage)
    }
}