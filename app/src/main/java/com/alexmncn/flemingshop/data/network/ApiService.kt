package com.alexmncn.flemingshop.data.network

import com.alexmncn.flemingshop.data.model.Article
import com.alexmncn.flemingshop.utils.Constans
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    // API endpoints

    // Ruta para obtener el total de artículos
    @GET("${Constans.BASE_URL}/articles/total")
    suspend fun getAllArticlesTotal(): Int

    // Ruta para buscar artículos
    @GET("${Constans.BASE_URL}/articles/all")
    suspend fun getAllArticles(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): List<Article>
}

