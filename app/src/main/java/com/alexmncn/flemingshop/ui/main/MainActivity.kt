package com.alexmncn.flemingshop.ui.main

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.alexmncn.flemingshop.data.db.AppDatabase
import com.alexmncn.flemingshop.data.db.DatabaseProvider
import com.alexmncn.flemingshop.data.network.AuthManager
import com.alexmncn.flemingshop.ui.components.MainBottomBar
import com.alexmncn.flemingshop.ui.components.MainTopBar
import com.alexmncn.flemingshop.ui.screens.BarcodeScannerScreen
import com.alexmncn.flemingshop.ui.screens.FamiliesScreen
import com.alexmncn.flemingshop.ui.screens.FeaturedArticlesScreen
import com.alexmncn.flemingshop.ui.screens.HomeScreen
import com.alexmncn.flemingshop.ui.screens.LoginScreen
import com.alexmncn.flemingshop.ui.screens.NewArticlesScreen
import com.alexmncn.flemingshop.ui.screens.SearchArticlesScreen
import com.alexmncn.flemingshop.ui.screens.ShoppingListScreen
import com.alexmncn.flemingshop.ui.screens.UserPanelScreen
import com.alexmncn.flemingshop.ui.screens.shared.DetailArticleScreen
import com.alexmncn.flemingshop.ui.theme.FlemingShopTheme

class MainActivity : AppCompatActivity() {
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide() // Hide default topbar with app name

        // Inicializar la base de datos
        db = DatabaseProvider.getDatabase(this)

        // Inicializamos la autenticación
        AuthManager.initialize(this)

        setContent {
            FlemingShopApp(db)
        }
    }
}

@Composable
fun FlemingShopApp(db: AppDatabase) {
    val navController = rememberNavController()

    FlemingShopTheme {
        Scaffold(
            topBar = { MainTopBar(navController) },
            content = { paddingValues ->
                NavHost(
                    navController = navController,
                    startDestination = "home",
                    modifier = Modifier.padding(paddingValues)
                ) {
                    composable("home") { HomeScreen(navController) }
                    composable("login") { LoginScreen(navController) }
                    composable("user_panel") { UserPanelScreen(navController) }
                    composable("featured_articles") { FeaturedArticlesScreen(navController) }
                    composable("new_articles") { NewArticlesScreen(navController) }
                    composable("families") { FamiliesScreen(navController) }
                    composable("families/family_articles/{codfam}/{nomfam}",
                            arguments = listOf(
                                navArgument("codfam") { type = NavType.IntType },
                                navArgument("nomfam") { type = NavType.StringType }
                            )
                    ) {
                        val codfam = it.arguments?.getInt("codfam") ?: 0
                        val nomfam = it.arguments?.getString("nomfam") ?: ""
                        val route = "family_articles/$codfam/$nomfam"

                        FamiliesScreen(navController, route)
                    }
                    composable("search_articles") { SearchArticlesScreen(navController) }
                    composable("search_articles/{query}",
                        arguments = listOf(navArgument("query") { type = NavType.StringType })
                    ) {
                        val query = it.arguments?.getString("query") ?: ""
                        SearchArticlesScreen(navController, query)
                    }
                    composable("barcode_scanner") { BarcodeScannerScreen(navController) }
                    composable("article_detail/{codebar}",
                        arguments = listOf(navArgument("codebar") { type = NavType.StringType })
                    ) {
                        val codebar = it.arguments?.getString("codebar") ?: ""
                        DetailArticleScreen(codebar, navController, db)
                    }
                    composable("shopping_list") { ShoppingListScreen(db, navController) }
                }
            },
            bottomBar = {
                Surface(
                    color = MaterialTheme.colorScheme.primary
                ) {
                    MainBottomBar(
                        navController,
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                    )
                }
            }

        )
    }
}