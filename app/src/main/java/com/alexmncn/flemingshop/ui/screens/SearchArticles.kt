package com.alexmncn.flemingshop.ui.screens

import android.util.Log
import android.widget.Space
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchArticlesScreen(navController: NavController) {
    val context = LocalContext.current
    val apiClient = ApiClient.provideOkHttpClient(context)
    val articleRepository: ArticleRepository by lazy { ArticleRepository(ApiService(apiClient)) }

    var allArticlesTotal by remember { mutableIntStateOf(0) }
    val searchAListName = "Resultados"
    var searchArticlesTotal by remember { mutableIntStateOf(0) }
    var searchArticles by remember { mutableStateOf<List<Article>>(emptyList()) }
    var currentPage by remember { mutableIntStateOf(1) }

    var placeholder by remember { mutableStateOf("Buscar entre todos los artículos") }
    var query by remember { mutableStateOf("") }
    var lastQuery by remember { mutableStateOf("def") }


    fun loadArticlesTotal() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                allArticlesTotal = articleRepository.getAllArticlesTotal()

                placeholder = "Buscar entre $allArticlesTotal artículos"
            } catch (e: Exception) {
                Log.e("error", e.toString())
            }
        }
    }

    LaunchedEffect(Unit) {
        loadArticlesTotal()
    }

    fun loadSearchArticles() {
        if (query != lastQuery) {
            Log.d("query", query)
            Log.d("lastQuery", lastQuery)
            currentPage = 1
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    searchArticlesTotal = articleRepository.getSearchArticlesTotal(search = query)
                    searchArticles = articleRepository.getSearchArticles(search = query, page = currentPage)

                    currentPage++
                    lastQuery = query
                } catch (e: Exception) {
                    Log.e("error", e.toString())
                }
            }
        } else {
            Log.d("query", query)
            Log.d("lastQuery", lastQuery)
            Log.d("currentPage", currentPage.toString())
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val articles = articleRepository.getSearchArticles(search = query, page = currentPage)
                    searchArticles = searchArticles + articles
                    currentPage++
                    lastQuery = query
                } catch (e: Exception) {
                    Log.e("error", e.toString())
                }
            }
        }
    }

    fun onSearch () {
        if (query != lastQuery) {
            loadSearchArticles()
        }
    }

    Column (
        modifier = Modifier
            .padding(horizontal = 10.dp) // Horizontal margin for the content only
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Barra de búsqueda con fondo transparente
            SearchBar(
                query = query,
                onQueryChange = { query = it; onSearch() },
                onSearch = { onSearch() },
                active = false,
                onActiveChange = {},
                placeholder = { Text(placeholder) },
                content = { },
                colors = SearchBarDefaults.colors(
                    containerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.width(10.dp))

            // Icono alineado a la derecha
            Icon(
                imageVector = Icons.Default.QrCodeScanner,
                contentDescription = "Escaner",
                tint = Color.Black,
                modifier = Modifier
                    .size(25.dp)
                    .clickable {
                        navController.navigate("barcode_scanner") {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = false
                        }
                    }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        ArticleList (
            articles = searchArticles,
            total = searchArticlesTotal,
            listName = searchAListName,
            onShowMore = { loadSearchArticles() },
            navController = navController
        )
    }
}