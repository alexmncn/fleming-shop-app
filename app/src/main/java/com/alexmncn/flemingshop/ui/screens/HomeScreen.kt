package com.alexmncn.flemingshop.ui.screens

import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController


@Composable
fun HomeScreen(navController: NavController) {
    val configuration = LocalConfiguration.current
    val collapsibleGreetingViewModel: CollapsibleGreetingViewModel = viewModel() // Instancia del viewmodel
    val isCollapsed by collapsibleGreetingViewModel.isCollapsed // Estado de si está colapsado o no
    var offsetY by remember { mutableFloatStateOf(0f) } // Desplazamiento temporal durante el gesto
    val screenHeight = configuration.screenHeightDp.dp
    val initialHeight = screenHeight
    val targetHeight = 100.dp

    // Altura animada al final del gesto
    val animatedHeight by animateDpAsState(
        targetValue = if (isCollapsed) targetHeight else initialHeight,
        animationSpec = tween(durationMillis = 500),
        label = "animatedHeight"
    )

    // Animación flotante
    val offsetY2 by rememberInfiniteTransition(label = "floating").animateFloat(
        initialValue = 0f,
        targetValue = 10f, // La distancia que se mueve hacia arriba y hacia abajo
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000, // Duración
                easing = EaseOut
            ),
            repeatMode = RepeatMode.Reverse // Movimiento de ida y vuelta
        ), label = ""
    )

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Bienvenida
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (isCollapsed) animatedHeight else initialHeight) // Usamos el valor animado para la altura/ Controlamos cuando se carga ya colapsado
                .background(MaterialTheme.colorScheme.primary)
                .pointerInput(Unit) {
                    detectVerticalDragGestures(
                        onDragEnd = {
                            if (offsetY < -(screenHeight.value - targetHeight.value) / 4) {
                                // Si se desliza suficiente hacia arriba, colapsar
                                collapsibleGreetingViewModel.collapse()
                                offsetY = 0f
                            } else {
                                // Si no, volver al estado original
                                offsetY = 0f
                            }
                        },
                        onDragCancel = {
                            offsetY = 0f // Resetear el desplazamiento si se cancela el gesto
                        },
                        onVerticalDrag = { _, dragAmount ->
                            offsetY += dragAmount
                        }
                    )
                }
        ) {

            // Texto de bienvenida
            Column (
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 20.dp),
                Arrangement.spacedBy(5.dp)
            ) {
                if (!isCollapsed) {
                    // Linea 1
                    Text(
                        text = "Bienvenido/a a",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                    )

                    // Linea 3
                    Text(
                        text = "Tienda Fleming",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 30.sp),
                    )
                } else {
                    // Linea 1
                    Text(
                        text = "Catálogo",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }

            // Indicador para deslizar hacia arriba cuando está expandido
            if (!isCollapsed) {
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 30.dp)
                        .fillMaxWidth()
                        .offset(y = offsetY2.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = "Explorar",
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                    Text(
                        text = "Desliza para explorar",
                        color = Color.White,
                        style = MaterialTheme.typography.titleSmall,
                    )
                }
            }
        }


        // Options
        Column(
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(modifier = Modifier.height(5.dp))

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
                    Text(text = "En esta sección puedes encontrar artículos seleccionados que te pueden interesar", style = MaterialTheme.typography.bodyMedium)
                }
            }

            // New Articles
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clickable { navController.navigate("new_articles") },
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                ) {
                    Text(text = "Novedades", style = MaterialTheme.typography.titleSmall)
                    Text(text = "En esta sección puedes encontrar articulos que han llegado recientemente a la tienda", style = MaterialTheme.typography.bodyMedium)
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
                    Text(text = "En esta sección puedes encontrar articulos agrupados por familias", style = MaterialTheme.typography.bodyMedium)
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
                    Text(text = "En esta sección puedes buscar entre mas de 8000 articulos por su descripción", style = MaterialTheme.typography.bodyMedium)
                }
            }

            Spacer(modifier = Modifier.height(5.dp))
        }
    }
}

// Modelo para el estado del mensaje de bienvenida (para mantener en la sesión)
class CollapsibleGreetingViewModel : ViewModel() {
    // El estado se mantiene durante toda la sesión
    private val _isCollapsed = mutableStateOf(false)
    val isCollapsed: State<Boolean> = _isCollapsed

    fun collapse() {
        _isCollapsed.value = true
    }
}
