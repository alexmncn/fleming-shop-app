package com.alexmncn.flemingshop.ui.main

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.alexmncn.flemingshop.data.model.Article
import com.alexmncn.flemingshop.data.network.ApiClient
import com.alexmncn.flemingshop.data.network.ApiService
import com.alexmncn.flemingshop.data.repository.ArticleRepository
import com.alexmncn.flemingshop.ui.screens.shared.SimpleArticlesScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NewArticlesActivity : AppCompatActivity() {
    private val apiClient = ApiClient.provideOkHttpClient()
    private val articleRepository: ArticleRepository by lazy { ArticleRepository(ApiService(apiClient)) }

    var currentPage = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide() // Hide default topbar with app name

        // Utilizamos remember para mantener el estado de los artículos y actualizar la UI
        setContent {
            val newAListName = "Novedades"
            var newArticles by remember { mutableStateOf<List<Article>>(emptyList()) }
            var newArticlesTotal by remember { mutableIntStateOf(0) }

            // Funcion para cargar articulos, por pagina
            fun loadNewArticles() {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        newArticlesTotal = articleRepository.getNewArticlesTotal()
                        val articles = articleRepository.getNewArticles(page=currentPage)
                        newArticles = newArticles + articles
                        currentPage++
                    } catch (e: Exception) {
                        Log.e("error", e.toString())
                    }
                }
            }

            // Llama a la función de actualización cuando se crea la actividad
            LaunchedEffect(Unit) {
                loadNewArticles()
            }

            SimpleArticlesScreen(
                articles = newArticles,
                total = newArticlesTotal,
                listName = newAListName,
                onShowMore = { loadNewArticles() }
            )
        }

    }
}