package com.alexmncn.flemingshop.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.alexmncn.flemingshop.data.model.Article
import com.alexmncn.flemingshop.data.network.ApiClient
import com.alexmncn.flemingshop.data.network.ApiService
import com.alexmncn.flemingshop.data.repository.CatalogRepository
import com.alexmncn.flemingshop.ui.components.ArticleList
import com.alexmncn.flemingshop.ui.components.CustomSearchBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun SearchArticlesScreen(navController: NavController) {
    val context = LocalContext.current
    val apiClient = ApiClient.provideOkHttpClient(context)
    val catalogRepository: CatalogRepository by lazy { CatalogRepository(ApiService(apiClient)) }

    // Todos los articulos
    val allAListName = "Todos"
    var allArticlesTotal by remember { mutableIntStateOf(0) }
    var allArticles by remember { mutableStateOf<List<Article>>(emptyList()) }
    var currentPageAll by remember { mutableIntStateOf(1) }

    // Articulos de busqueda
    var searchArticlesTotal by remember { mutableIntStateOf(0) }
    var searchArticles by remember { mutableStateOf<List<Article>>(emptyList()) }
    var currentPage by remember { mutableIntStateOf(1) }
    var isLoading by remember { mutableStateOf(false) }

    var placeholder by remember { mutableStateOf("Buscar entre todos los artículos") }
    var query by remember { mutableStateOf("") }
    var lastQuery by remember { mutableStateOf("def") }
    val searchAListName = "Resultados de '${query}'"

    fun loadAllArticles() {
        CoroutineScope(Dispatchers.IO).launch {
            isLoading = true

            try {
                allArticlesTotal = catalogRepository.getAllArticlesTotal()
                var articles = catalogRepository.getAllArticles(page = currentPageAll)
                allArticles = allArticles + articles

                currentPageAll++
                placeholder = "Buscar entre $allArticlesTotal artículos"
            } catch (e: Exception) {
                Log.e("error", e.toString())
            }

            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        loadAllArticles()
    }

    fun loadSearchArticles() {
        if (query != lastQuery) {
            currentPage = 1
            CoroutineScope(Dispatchers.IO).launch {
                isLoading = true

                try {
                    searchArticlesTotal = catalogRepository.getSearchArticlesTotal(search = query)
                    searchArticles = catalogRepository.getSearchArticles(search = query, page = currentPage)
                    currentPage++
                } catch (e: Exception) {
                    Log.e("error", e.toString())
                    searchArticles = emptyList()
                }

                isLoading = false
            }
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                isLoading = true

                try {
                    val articles = catalogRepository.getSearchArticles(search = query, page = currentPage)
                    searchArticles = searchArticles + articles
                    currentPage++
                } catch (e: Exception) {
                    Log.e("error", e.toString())
                }

                isLoading = false
            }
        }
        lastQuery = query
    }

    fun onSearch () {
        if (query != lastQuery) {
            loadSearchArticles()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Contenedor para padding con fondo
        Column (
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primary)
        ) {
            // Barra de busqueda + escaner
            Row(
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 5.dp, start = 10.dp, end = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CustomSearchBar(
                    query = query,
                    onQueryChange = { query = it; onSearch() },
                    onSearch = { onSearch() },
                    placeholder = placeholder,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(8f),
                )

                Spacer(modifier = Modifier.width(10.dp))

                // Boton de escaner
                Icon(
                    imageVector = Icons.Default.QrCodeScanner,
                    contentDescription = "Escaner",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .weight(1f)
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

            Spacer(modifier = Modifier.size(5.dp))
        }

        ArticleList (
            articles = if (query.isEmpty() && searchArticles.isEmpty()) allArticles else searchArticles,
            total = if (query.isEmpty() && searchArticles.isEmpty()) allArticlesTotal else searchArticlesTotal,
            listName = if (query.isEmpty() && searchArticles.isEmpty()) allAListName else searchAListName,
            isLoading = isLoading,
            onShowMore = { if (query.isEmpty() && searchArticles.isEmpty()) loadAllArticles() else loadSearchArticles() },
            navController = navController
        )
    }
}