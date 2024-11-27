package com.alexmncn.flemingshop.ui.screens

import android.content.Context
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.alexmncn.flemingshop.data.network.ApiClient
import com.alexmncn.flemingshop.data.network.ApiService
import com.alexmncn.flemingshop.data.network.AuthManager
import com.alexmncn.flemingshop.data.repository.ArticleRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun UserPanelScreen(navController: NavController) {
    val context = LocalContext.current
    val apiClient = ApiClient.provideOkHttpClient(context)
    val articleRepository: ArticleRepository by lazy { ArticleRepository(ApiService(apiClient)) }

    fun logout(context: Context, articleRepository: ArticleRepository) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = articleRepository.logout()
                AuthManager.clearSession(context)

                withContext(Dispatchers.Main) {
                    navController.navigate("home")
                    Toast.makeText(context, "Has cerrado sesión", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                Log.e("error", e.toString())
            }
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Bottom
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
}