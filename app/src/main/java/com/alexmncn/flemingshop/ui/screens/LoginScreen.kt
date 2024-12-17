package com.alexmncn.flemingshop.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.alexmncn.flemingshop.data.network.ApiClient
import com.alexmncn.flemingshop.data.network.ApiService
import com.alexmncn.flemingshop.data.network.AuthManager
import com.alexmncn.flemingshop.data.repository.AuthRepository
import com.alexmncn.flemingshop.ui.theme.Blue50
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current
    val apiClient = ApiClient.provideOkHttpClient(context)
    val authRepository: AuthRepository by lazy { AuthRepository(ApiService(apiClient)) }
    var isLoading by remember { mutableStateOf(false) }

    val username = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    fun login(username: String, password: String) {
        CoroutineScope(Dispatchers.IO).launch {
            isLoading = true

            try {
                val response = authRepository.login(username, password)
                AuthManager.saveSession(context, response.token) // Guardamos la info de sesion

                withContext(Dispatchers.Main) {
                    navController.navigate("home")
                    Toast.makeText(context, "Has iniciado sesión", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, e.message.toString(), Toast.LENGTH_SHORT).show()
                }
            }

            isLoading = false
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Inicia Sesión", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(20.dp))

        TextField(
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Blue50,
                unfocusedContainerColor = Blue50,
            ),
            value = username.value,
            onValueChange = { username.value = it },
            label = { Text("Usuario") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Blue50,
                unfocusedContainerColor = Blue50,
            ),
            value = password.value,
            onValueChange = { password.value = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(30.dp))

        Card(
            onClick = {
                login(username.value, password.value)
            },
            modifier = Modifier
                .fillMaxWidth(),
            enabled = !isLoading, // Deshabilita el boton si se estan cargango el login
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Enviar", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }
    }
}
