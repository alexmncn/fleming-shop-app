package com.alexmncn.flemingshop.ui.main

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alexmncn.flemingshop.data.model.Article
import com.alexmncn.flemingshop.data.network.ApiService
import com.alexmncn.flemingshop.data.repository.ArticleRepository
import com.alexmncn.flemingshop.ui.components.ArticleList
import com.alexmncn.flemingshop.ui.theme.FlemingShopTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.math.BigInteger
import java.util.Date

class MainActivity : AppCompatActivity() {
    private val articleRepository: ArticleRepository by lazy {
        ArticleRepository(ApiService)
    }

    var currentPage = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide() // Hide default topbar with app name

        // Utilizamos remember para mantener el estado de los artículos y actualizar la UI
        setContent {
            val featuredAListName = "Destacado"
            var featuredArticles by remember { mutableStateOf<List<Article>>(emptyList()) }
            var featuredArticlesTotal by remember { mutableIntStateOf(0) }

            fun loadFeaturedArticles() {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        featuredArticlesTotal = articleRepository.getFeaturedArticlesTotal()
                        val articles = articleRepository.getFeaturedArticles(page=currentPage)
                        featuredArticles = featuredArticles + articles
                        currentPage++
                        Log.d("articles", featuredArticlesTotal.toString())
                    } catch (e: Exception) {
                        Log.e("error", e.toString())
                    }
                }
            }

            // Llama a la función de actualización cuando se crea la actividad
            LaunchedEffect(Unit) {
                loadFeaturedArticles()
            }

            MainScreen(featuredArticlesTotal, featuredArticles, featuredAListName, onShowMore = { loadFeaturedArticles() })
        }

    }
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(featuredArticlesTotal: Int, featuredArticles: List<Article>, featuredListName: String, onShowMore: () -> Unit) {
    FlemingShopTheme {
        Scaffold(
            topBar = { MainTopBar() },
            content = { paddingValues ->
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .padding(horizontal = 10.dp) // Horizontal margin for the content only
                ) {
                    ArticleList(total = featuredArticlesTotal, articles = featuredArticles, listName = featuredListName, onShowMore)
                }
            },
        )
    }
}

@Preview
@Composable
fun PreviewMainScreen() {
    val sampleArticles = listOf(
        Article(
            ref = BigInteger("1000000001"),
            codebar = BigInteger("1"),
            detalle = "Artículo de prueba A",
            codfam = 1,
            pcosto = 10.50f,
            pvp = 15.75f,
            stock = 100,
            factualizacion = Date(),
            destacado = true,
            hidden = false
        ),
        Article(
            ref = BigInteger("1000000002"),
            codebar = BigInteger("2"),
            detalle = "Artículo de prueba B",
            codfam = 2,
            pcosto = 20.00f,
            pvp = 30.00f,
            stock = 50,
            factualizacion = Date(),
            destacado = false,
            hidden = false
        ),
        Article(
            ref = BigInteger("1000000003"),
            codebar = BigInteger("3"),
            detalle = "Artículo de prueba C",
            codfam = 3,
            pcosto = 5.00f,
            pvp = 7.50f,
            stock = 200,
            factualizacion = Date(),
            destacado = true,
            hidden = true
        ),
        Article(
            ref = BigInteger("1000000004"),
            codebar = BigInteger("4"),
            detalle = "Artículo de prueba D",
            codfam = 4,
            pcosto = 12.25f,
            pvp = 18.99f,
            stock = 25,
            factualizacion = Date(),
            destacado = false,
            hidden = false
        ),
        Article(
            ref = BigInteger("1000000005"),
            codebar = BigInteger("5"),
            detalle = "Artículo de prueba E",
            codfam = 5,
            pcosto = 8.75f,
            pvp = 13.50f,
            stock = 75,
            factualizacion = Date(),
            destacado = true,
            hidden = false
        )
    )
    MainScreen(sampleArticles.size+20, featuredArticles = sampleArticles, "Destacado", onShowMore = {})
}