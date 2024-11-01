package com.alexmncn.flemingshop.ui.main

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.alexmncn.flemingshop.data.model.Article
import com.alexmncn.flemingshop.data.network.ApiService
import com.alexmncn.flemingshop.ui.components.ArticleList
import com.alexmncn.flemingshop.ui.theme.FlemingShopTheme
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.math.BigInteger
import java.util.Date

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide() // Hide default topbar with app name

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

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(articles: List<Article>) {
    FlemingShopTheme {
        Scaffold(
            topBar = { MainTopBar() },
            content = { ArticleList(articles = articles) }
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
    MainScreen(articles = sampleArticles)
}