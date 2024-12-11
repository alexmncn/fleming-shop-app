package com.alexmncn.flemingshop.ui.screens

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.alexmncn.flemingshop.data.model.Article
import com.alexmncn.flemingshop.data.network.ApiClient
import com.alexmncn.flemingshop.data.network.ApiService
import com.alexmncn.flemingshop.data.repository.CatalogRepository
import com.alexmncn.flemingshop.ui.components.ArticleList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun FeaturedArticlesScreen(navController: NavController) {
    val context = LocalContext.current
    val apiClient = ApiClient.provideOkHttpClient(context)
    val catalogRepository: CatalogRepository by lazy { CatalogRepository(ApiService(apiClient)) }

    val featuredAListName = "Destacado"
    var featuredArticles by remember { mutableStateOf<List<Article>>(emptyList()) }
    var featuredArticlesTotal by remember { mutableIntStateOf(0) }
    var currentPage by remember { mutableIntStateOf(1) }
    var isLoading by remember { mutableStateOf(false) }

    // Funcion para cargar articulos, por pagina
    fun loadFeaturedArticles() {
        CoroutineScope(Dispatchers.IO).launch {
            isLoading = true

            try {
                featuredArticlesTotal = catalogRepository.getFeaturedArticlesTotal()
                val articles = catalogRepository.getFeaturedArticles(page=currentPage)
                featuredArticles = featuredArticles + articles
                currentPage++
            } catch (e: Exception) {
                Log.e("error", e.toString())
            }

            isLoading = false
        }
    }

    // Llama a la función de actualización cuando se crea la actividad
    LaunchedEffect(Unit) {
        loadFeaturedArticles()
    }

    ArticleList (
        articles = featuredArticles,
        total = featuredArticlesTotal,
        listName = featuredAListName,
        isLoading = isLoading,
        onShowMore = { loadFeaturedArticles() },
        navController = navController
    )
}