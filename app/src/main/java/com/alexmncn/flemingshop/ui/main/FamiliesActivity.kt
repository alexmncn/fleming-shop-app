package com.alexmncn.flemingshop.ui.main

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.alexmncn.flemingshop.data.model.Article
import com.alexmncn.flemingshop.data.model.Family
import com.alexmncn.flemingshop.data.network.ApiClient
import com.alexmncn.flemingshop.data.network.ApiService
import com.alexmncn.flemingshop.data.repository.ArticleRepository
import com.alexmncn.flemingshop.ui.components.FamilyList
import com.alexmncn.flemingshop.ui.components.MainBottomBar
import com.alexmncn.flemingshop.ui.components.MainTopBar
import com.alexmncn.flemingshop.ui.screens.shared.SimpleArticlesScreen
import com.alexmncn.flemingshop.ui.theme.FlemingShopTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FamiliesActivity : AppCompatActivity() {
    private val apiClient = ApiClient.provideOkHttpClient(this)
    private val articleRepository: ArticleRepository by lazy { ArticleRepository(ApiService(apiClient)) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide() // Hide default topbar with app name

        setContent {
            var families by remember { mutableStateOf<List<Family>>(emptyList()) }

            fun loadFamilies() {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        families = articleRepository.getFamilies()
                    } catch (e: Exception) {
                        Log.e("error", e.toString())
                    }
                }
            }

            LaunchedEffect(Unit) {
                loadFamilies()
            }

            // Navegador entre componentes en la misma activity
            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = "families"
            ) {
                composable("families") {
                    FamiliesScreen(
                        families = families,
                        onShowFamily = { codfam, nomfam ->
                            navController.navigate("family_articles/$codfam/$nomfam")
                        }
                    )
                }
                composable(
                    route = "family_articles/{codfam}/{nomfam}",
                    arguments = listOf(
                        navArgument("codfam") { type = NavType.IntType },
                        navArgument("nomfam") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val codfam = backStackEntry.arguments?.getInt("codfam") ?: 0
                    val nomfam = backStackEntry.arguments?.getString("nomfam") ?: ""

                    FamilyArticlesScreen(codfam, nomfam)
                }
            }
        }
    }
}


@Composable
fun FamiliesScreen(families: List<Family>, onShowFamily: (codfam: Int, nomfam: String) -> Unit) {
    FlemingShopTheme {
        Scaffold(
            topBar = { MainTopBar() },
            content = { paddingValues ->
                Column(
                    modifier = Modifier
                        .padding(paddingValues) // Safe zone from topbar
                        .padding(horizontal = 10.dp) // Horizontal margin for the content only
                ) {
                    FamilyList(
                        families = families,
                        onShowFamily = { codfam, nomfam -> onShowFamily(codfam, nomfam)}
                    )
                }
            },
            bottomBar = { MainBottomBar() }
        )
    }
}

@Composable
fun FamilyArticlesScreen(codfam: Int, nomfam: String) {
    val context = LocalContext.current
    val apiClient = ApiClient.provideOkHttpClient(context)
    val articleRepository: ArticleRepository by lazy { ArticleRepository(ApiService(apiClient)) }

    var familyArticles by remember { mutableStateOf<List<Article>>(emptyList()) }
    var familyArticlesTotal by remember { mutableIntStateOf(0) }
    var currentPage by remember { mutableIntStateOf(1) }

    // Funcion para cargar articulos, por pagina
    fun loadFamilyArticles() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                familyArticlesTotal = articleRepository.getFamilyArticlesTotal(codfam)
                val articles = articleRepository.getFamilyArticles(familyId = codfam, page = currentPage)
                familyArticles = familyArticles + articles
                currentPage++
            } catch (e: Exception) {
                Log.e("error", e.toString())
            }
        }
    }

    // Llama a la función de actualización cuando se crea la actividad
    LaunchedEffect(Unit) {
        loadFamilyArticles()
    }

    SimpleArticlesScreen(
        articles = familyArticles,
        total = familyArticlesTotal,
        listName = nomfam,
        onShowMore = { loadFamilyArticles() }
    )
}