package com.alexmncn.flemingshop.ui.main

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.alexmncn.flemingshop.R
import com.alexmncn.flemingshop.data.model.Article
import com.alexmncn.flemingshop.data.network.ApiService
import com.alexmncn.flemingshop.data.repository.ArticleRepository
import com.alexmncn.flemingshop.ui.screens.shared.DetailArticleScreen
import com.alexmncn.flemingshop.ui.screens.shared.SimpleArticlesScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailArticleActivity : AppCompatActivity() {
    private val articleRepository: ArticleRepository by lazy {
        ArticleRepository(ApiService)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide() // Hide default topbar with app name

        val codebar = intent.getStringExtra("codebar")

        setContent {
            var article by remember { mutableStateOf<Article?>(null) }

            // Funcion para cargar articulos, por pagina
            fun loadFeaturedArticles() {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        article = articleRepository.getSearchArticles(search = codebar.toString(), filter = "codebar")[0]
                    } catch (e: Exception) {
                        Log.e("error", e.toString())
                    }
                }
            }

            // Llama a la función de actualización cuando se crea la actividad
            LaunchedEffect(Unit) {
                loadFeaturedArticles()
            }

            article?.let { DetailArticleScreen(it) }
        }
    }

}