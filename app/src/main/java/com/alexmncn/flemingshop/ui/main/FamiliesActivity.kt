package com.alexmncn.flemingshop.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.alexmncn.flemingshop.R
import com.alexmncn.flemingshop.data.model.Article
import com.alexmncn.flemingshop.data.model.Family
import com.alexmncn.flemingshop.data.network.ApiClient
import com.alexmncn.flemingshop.data.network.ApiService
import com.alexmncn.flemingshop.data.repository.ArticleRepository
import com.alexmncn.flemingshop.ui.components.ArticleCard
import com.alexmncn.flemingshop.ui.components.FamilyCard
import com.alexmncn.flemingshop.ui.components.FamilyList
import com.alexmncn.flemingshop.ui.components.MainBottomBar
import com.alexmncn.flemingshop.ui.components.MainTopBar
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

            FamiliesScreen(families = families)
        }
    }
}


@Composable
fun FamiliesScreen(families: List<Family>) {
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
                        families = families
                    )
                }
            },
            bottomBar = { MainBottomBar() }
        )
    }
}