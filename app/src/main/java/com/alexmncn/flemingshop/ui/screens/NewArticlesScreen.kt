package com.alexmncn.flemingshop.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.alexmncn.flemingshop.data.model.Article
import com.alexmncn.flemingshop.data.network.ApiClient
import com.alexmncn.flemingshop.data.network.ApiService
import com.alexmncn.flemingshop.data.repository.ArticleRepository
import com.alexmncn.flemingshop.ui.components.ArticleList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun NewArticlesScreen(navController: NavController) {
    val context = LocalContext.current
    val apiClient = ApiClient.provideOkHttpClient(context)
    val articleRepository: ArticleRepository by lazy { ArticleRepository(ApiService(apiClient)) }

    val newAListName = "Novedades"
    var newArticles by remember { mutableStateOf<List<Article>>(emptyList()) }
    var newArticlesTotal by remember { mutableIntStateOf(0) }
    var currentPage by remember { mutableIntStateOf(1) }

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

    Column (
        modifier = Modifier
            .padding(horizontal = 10.dp) // Horizontal margin for the content only
    ) {
        ArticleList (
            articles = newArticles,
            total = newArticlesTotal,
            listName = newAListName,
            onShowMore = { loadNewArticles() },
            navController = navController
        )
    }
}