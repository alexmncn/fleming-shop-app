package com.alexmncn.flemingshop.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.alexmncn.flemingshop.data.model.Article
import com.alexmncn.flemingshop.data.network.ApiClient
import com.alexmncn.flemingshop.data.network.ApiService
import com.alexmncn.flemingshop.data.repository.CatalogRepository
import com.alexmncn.flemingshop.ui.components.ArticleList
import com.alexmncn.flemingshop.ui.theme.Blue100
import com.alexmncn.flemingshop.ui.theme.Blue200
import com.alexmncn.flemingshop.ui.theme.Blue300
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchArticlesScreen(navController: NavController) {
    val context = LocalContext.current
    val apiClient = ApiClient.provideOkHttpClient(context)
    val catalogRepository: CatalogRepository by lazy { CatalogRepository(ApiService(apiClient)) }

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
                allArticlesTotal = catalogRepository.getAllArticlesTotal()

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
            currentPage = 1
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    searchArticlesTotal = catalogRepository.getSearchArticlesTotal(search = query)
                    searchArticles = catalogRepository.getSearchArticles(search = query, page = currentPage)
                    currentPage++
                } catch (e: Exception) {
                    Log.e("error", e.toString())
                    searchArticles = emptyList()
                }
            }
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val articles = catalogRepository.getSearchArticles(search = query, page = currentPage)
                    searchArticles = searchArticles + articles
                    currentPage++
                } catch (e: Exception) {
                    Log.e("error", e.toString())
                }
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
        }

        ArticleList (
            articles = searchArticles,
            total = searchArticlesTotal,
            listName = searchAListName,
            onShowMore = { loadSearchArticles() },
            navController = navController
        )
    }
}

@Composable
fun CustomSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,

) {
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.onPrimary, RoundedCornerShape(8.dp))
            .padding(10.dp)
    ) {

        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            textStyle = MaterialTheme.typography.bodyLarge,
            singleLine = true,
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            decorationBox = { innerTextField ->
                if (query.isEmpty()) {
                    Text(
                        text = placeholder,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                innerTextField()
            },
            modifier = Modifier
                .onKeyEvent {
                    if (it.nativeKeyEvent.keyCode == android.view.KeyEvent.KEYCODE_ENTER) {
                        onSearch()
                        true
                    } else false
                }
        )

    }
}
