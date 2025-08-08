package com.alexmncn.flemingshop.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.alexmncn.flemingshop.data.network.ApiService
import com.google.gson.Gson
import java.io.File

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

    fun uploadArticleImage(context: Context, bitmap: Bitmap, codebar: String, isMain: Boolean = false, convert: Boolean = true, keepOriginal: Boolean = false): Boolean {
        // Convertimos la imagen a archivo
        val file = File(context.cacheDir, "image.jpeg")
        file.outputStream().use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        }
        val response = apiService.uploadArticleImage(file, codebar, isMain, convert, keepOriginal)
        Log.d("images", response.toString())
        return response?.isSuccessful == true
    }
}