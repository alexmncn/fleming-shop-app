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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.alexmncn.flemingshop.data.model.Article
import com.alexmncn.flemingshop.data.model.Family
import com.alexmncn.flemingshop.data.network.ApiClient
import com.alexmncn.flemingshop.data.network.ApiService
import com.alexmncn.flemingshop.data.repository.CatalogRepository
import com.alexmncn.flemingshop.ui.components.ArticleList
import com.alexmncn.flemingshop.ui.components.FamilyList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun FamiliesScreen(navController: NavController) {
    // Navegador entre componentes en la misma activity
    val navControllerInt = rememberNavController()

    NavHost(
        navController = navControllerInt,
        startDestination = "families"
    ) {
        composable("families") {
            FamilyListScreen(
                onShowFamily = { codfam, nomfam ->
                    navControllerInt.navigate("family_articles/$codfam/$nomfam")
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

            FamilyArticlesScreen(codfam, nomfam, navController)
        }
    }
}


@Composable
fun FamilyListScreen(onShowFamily: (codfam: Int, nomfam: String) -> Unit) {
    val context = LocalContext.current
    val apiClient = ApiClient.provideOkHttpClient(context)
    val catalogRepository: CatalogRepository by lazy { CatalogRepository(ApiService(apiClient)) }
    var families by remember { mutableStateOf<List<Family>>(emptyList()) }

    fun loadFamilies() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                families = catalogRepository.getFamilies()
            } catch (e: Exception) {
                Log.e("error", e.toString())
            }
        }
    }

    LaunchedEffect(Unit) {
        loadFamilies()
    }

    Column(
        modifier = Modifier
            .padding(horizontal = 10.dp) // Horizontal margin for the content only
    ) {
        FamilyList(
            families = families,
            onShowFamily = { codfam, nomfam -> onShowFamily(codfam, nomfam) }
        )
    }
}

@Composable
fun FamilyArticlesScreen(codfam: Int, nomfam: String, navController: NavController) {
    val context = LocalContext.current
    val apiClient = ApiClient.provideOkHttpClient(context)
    val catalogRepository: CatalogRepository by lazy { CatalogRepository(ApiService(apiClient)) }

    var familyArticles by remember { mutableStateOf<List<Article>>(emptyList()) }
    var familyArticlesTotal by remember { mutableIntStateOf(0) }
    var currentPage by remember { mutableIntStateOf(1) }

    // Funcion para cargar articulos, por pagina
    fun loadFamilyArticles() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                familyArticlesTotal = catalogRepository.getFamilyArticlesTotal(codfam)
                val articles = catalogRepository.getFamilyArticles(familyId = codfam, page = currentPage)
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

    Column(
        modifier = Modifier
            .padding(horizontal = 10.dp) // Horizontal margin for the content only
    ) {
        ArticleList(
            articles = familyArticles,
            total = familyArticlesTotal,
            listName = nomfam,
            onShowMore = { loadFamilyArticles() },
            navController = navController
        )
    }
}