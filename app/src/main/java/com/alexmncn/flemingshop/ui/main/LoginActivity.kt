package com.alexmncn.flemingshop.ui.main

import android.content.Context
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.alexmncn.flemingshop.data.network.ApiClient
import com.alexmncn.flemingshop.data.network.ApiService
import com.alexmncn.flemingshop.data.repository.ArticleRepository
import com.alexmncn.flemingshop.ui.components.MainBottomBar
import com.alexmncn.flemingshop.ui.components.MainTopBar
import com.alexmncn.flemingshop.ui.theme.FlemingShopTheme
import com.alexmncn.flemingshop.data.network.AuthManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private val client = ApiClient.provideOkHttpClient(this)
    private val apiService = ApiService(client)
    private val articleRepository: ArticleRepository by lazy { ArticleRepository(apiService) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide() // Hide default topbar with app name

        setContent {
            LoginScreen(articleRepository)
        }
    }
}

fun login(username: String, password: String, context: Context, articleRepository: ArticleRepository) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = articleRepository.login(username, password)
            AuthManager.saveSession(context, response.token) // Guardamos la info de sesion

            Looper.prepare()
            Toast.makeText(context, "Has iniciado sesión", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e("error", e.toString())
        }
    }
}


@Composable
fun LoginScreen(
    articleRepository: ArticleRepository
) {
    val context = LocalContext.current
    val username = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

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
                    Text("Iniciar Sesión", style = MaterialTheme.typography.titleMedium)

                    Spacer(modifier = Modifier.height(20.dp))

                    TextField(
                        value = username.value,
                        onValueChange = { username.value = it },
                        label = { Text("Usuario") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    TextField(
                        value = password.value,
                        onValueChange = { password.value = it },
                        label = { Text("Contraseña") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            login(username.value, password.value, context, articleRepository)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Enviar")
                    }

                }
            },
            bottomBar = { MainBottomBar() }
        )
    }
}