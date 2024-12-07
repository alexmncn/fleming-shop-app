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
fun NewArticlesScreen(navController: NavController) {
    val context = LocalContext.current
    val apiClient = ApiClient.provideOkHttpClient(context)
    val catalogRepository: CatalogRepository by lazy { CatalogRepository(ApiService(apiClient)) }

    val newAListName = "Novedades"
    var newArticles by remember { mutableStateOf<List<Article>>(emptyList()) }
    var newArticlesTotal by remember { mutableIntStateOf(0) }
    var currentPage by remember { mutableIntStateOf(1) }

    // Funcion para cargar articulos, por pagina
    fun loadNewArticles() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                newArticlesTotal = catalogRepository.getNewArticlesTotal()
                val articles = catalogRepository.getNewArticles(page=currentPage)
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

    ArticleList (
        articles = newArticles,
        total = newArticlesTotal,
        listName = newAListName,
        onShowMore = { loadNewArticles() },
        navController = navController
    )
}