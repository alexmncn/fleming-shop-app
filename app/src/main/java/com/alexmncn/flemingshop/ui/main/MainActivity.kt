package com.alexmncn.flemingshop.ui.main

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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
import com.alexmncn.flemingshop.ui.screens.UserPanelScreen
import com.alexmncn.flemingshop.ui.screens.shared.DetailArticleScreen
import com.alexmncn.flemingshop.ui.theme.FlemingShopTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide() // Hide default topbar with app name

        // Inicializamos la autenticación
        AuthManager.initialize(this)

        setContent {
            FlemingShopApp()
        }
    }
}

@Composable
fun FlemingShopApp() {
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
                    composable("search_articles") { SearchArticlesScreen(navController) }
                    composable("barcode_scanner") { BarcodeScannerScreen(navController) }
                    composable("article_detail/{codebar}",
                        arguments = listOf(navArgument("codebar") { type = NavType.StringType })
                    ) {
                        val codebar = it.arguments?.getString("codebar") ?: ""
                        DetailArticleScreen(codebar)
                    }
                }
            },
            bottomBar = { MainBottomBar(navController) }
        )
    }
}