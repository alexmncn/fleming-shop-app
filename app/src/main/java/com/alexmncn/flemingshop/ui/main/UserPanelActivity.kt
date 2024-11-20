package com.alexmncn.flemingshop.ui.main

import android.content.Context
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.alexmncn.flemingshop.R
import com.alexmncn.flemingshop.data.network.ApiClient
import com.alexmncn.flemingshop.data.network.ApiService
import com.alexmncn.flemingshop.data.network.AuthManager
import com.alexmncn.flemingshop.data.repository.ArticleRepository
import com.alexmncn.flemingshop.ui.components.MainBottomBar
import com.alexmncn.flemingshop.ui.components.MainTopBar
import com.alexmncn.flemingshop.ui.theme.FlemingShopTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserPanelActivity : AppCompatActivity() {
    private val client = ApiClient.provideOkHttpClient()
    private val apiService = ApiService(client)
    private val articleRepository: ArticleRepository by lazy { ArticleRepository(apiService) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide() // Hide default topbar with app name

        setContent {
            UserPanelScreen(articleRepository)
        }
    }
}

fun logout(context: Context, articleRepository: ArticleRepository) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = articleRepository.logout()
            AuthManager.clearSession(context)

            Looper.prepare()
            Toast.makeText(context, "Has cerrado sesión", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e("error", e.toString())
        }
    }
}



@Composable
fun UserPanelScreen(articleRepository: ArticleRepository) {
    val context = LocalContext.current

    FlemingShopTheme {
        Scaffold(
            topBar = { MainTopBar() },
            content = { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = {
                            logout(context, articleRepository)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Cerrar sesion")
                    }

                }
            },
            bottomBar = { MainBottomBar() }
        )
    }
}