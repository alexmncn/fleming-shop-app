package com.alexmncn.flemingshop.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import com.alexmncn.flemingshop.data.model.Article
import com.alexmncn.flemingshop.data.network.ApiService
import com.alexmncn.flemingshop.ui.components.ArticleList
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val gson = Gson()

        // Utilizamos remember para mantener el estado de los artículos y actualizar la UI
        setContent {
            var featuredArticles by remember { mutableStateOf<List<Article>>(emptyList()) }

            // Llamamos a la función para hacer la solicitud en una corrutina
            LaunchedEffect(Unit) {
                CoroutineScope(Dispatchers.IO).launch {
                    val response = ApiService.getFeaturedArticles()
                    if (response != null && response.isSuccessful) {
                        val responseData = response.body?.string()

                        val listType = object : TypeToken<List<Article>>() {}.type
                        featuredArticles = gson.fromJson(responseData, listType)

                        Log.d("ApiService", "Articles: $featuredArticles")
                    } else {
                        Log.e("ApiService", "Error: ${response?.code}")
                    }
                }
            }

            MainScreen(articles = featuredArticles)
        }
    }
}

@Composable
fun MainScreen(articles: List<Article>) {
    ArticleList(articles = articles)
}