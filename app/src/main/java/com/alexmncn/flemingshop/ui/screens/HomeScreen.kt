package com.alexmncn.flemingshop.ui.screens

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun HomeScreen(navController: NavController) {
    var viewOptions by remember { mutableStateOf(false) }

    // Altura animada cuando está colapsado
    val boxHeight by animateDpAsState(
        targetValue = if (viewOptions) 200.dp else LocalConfiguration.current.screenHeightDp.dp,
        animationSpec = tween(durationMillis = 500) // Duración de la animación
    )

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(boxHeight) // Altura animada
                .background(MaterialTheme.colorScheme.primary)
        ) {
            // Texto de bienvenida
            Text(
                text = "Bienvenido/a a \nTienda Fleming",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(50.dp)
            )

            // Botón para cambiar estado
            if (!viewOptions) {
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 20.dp)
                        .fillMaxWidth()
                        .clickable { viewOptions = true },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = "Explorar",
                        tint = Color.White,
                        modifier = Modifier.size(25.dp)
                    )
                    Text(
                        text = "Explora nuestro catálogo",
                        color = Color.White,
                        style = MaterialTheme.typography.titleSmall,
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // Featured Articles
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clickable { navController.navigate("featured_articles") },
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                ) {
                    Text(text = "Articulos destacados", style = MaterialTheme.typography.titleSmall)
                    Text(text = "En esta sección puedes encontrar artículos seleccionados que te pueden interesar", style = MaterialTheme.typography.bodyMedium,)
                }
            }

            // New Articles
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clickable { navController.navigate("families") },
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                ) {
                    Text(text = "Novedades", style = MaterialTheme.typography.titleSmall)
                    Text(text = "En esta sección puedes encontrar articulos que han llegado recientemente a la tienda", style = MaterialTheme.typography.bodyMedium,)
                }
            }

            // Families
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clickable { navController.navigate("families") },
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                ) {
                    Text(text = "Familias", style = MaterialTheme.typography.titleSmall)
                    Text(text = "En esta sección puedes encontrar articulos agrupados por familias", style = MaterialTheme.typography.bodyMedium,)
                }
            }

            // Search Articles
            Card (
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clickable { navController.navigate("search_articles") },
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                ) {
                    Text(text = "Buscar articulos", style = MaterialTheme.typography.titleSmall)
                    Text(text = "En esta sección puedes buscar entre mas de 8000 articulos por su descripción", style = MaterialTheme.typography.bodyMedium,)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}