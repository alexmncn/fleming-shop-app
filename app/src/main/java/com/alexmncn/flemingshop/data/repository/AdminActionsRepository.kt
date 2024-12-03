package com.alexmncn.flemingshop.data.repository

import com.alexmncn.flemingshop.data.network.ApiService
import com.google.gson.Gson

class AdminActionsRepository(private val apiService: ApiService) {
    private val gson = Gson()

    // Admin actions func

    fun featureArticle(codebar: String): Boolean {
        val featured = true
        val response = apiService.featureArticle(codebar, featured)
        return response?.isSuccessful == true
    }

    fun unfeatureArticle(codebar: String): Boolean {
        val featured = false
        val response = apiService.featureArticle(codebar, featured)
        return response?.isSuccessful == true
    }


    fun hideArticle(codebar: String): Boolean {
        val hidden = true
        val response = apiService.hideArticle(codebar, hidden)
        return response?.isSuccessful == true
    }

    fun unhideArticle(codebar: String): Boolean {
        val hidden = false
        val response = apiService.hideArticle(codebar, hidden)
        return response?.isSuccessful == true
    }


    fun hideFamily(codfam: String, recursive: Boolean = true): Boolean {
        val hidden = true
        val response = apiService.hideFamily(codfam, hidden, recursive)
        return response?.isSuccessful == true
    }

    fun unhideFamily(codfam: String, recursive: Boolean = true): Boolean {
        val hidden = false
        val response = apiService.hideFamily(codfam, hidden, recursive)
        return response?.isSuccessful == true
    }


    fun hideFamilyArticles(codfam: String): Boolean {
        val hidden = true
        val response = apiService.hideFamilyArticles(codfam, hidden)
        return response?.isSuccessful == true
    }

    fun unhideFamilyArticles(codfam: String): Boolean {
        val hidden = false
        val response = apiService.hideFamilyArticles(codfam, hidden)
        return response?.isSuccessful == true
    }

}