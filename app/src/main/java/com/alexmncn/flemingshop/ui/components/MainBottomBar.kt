package com.alexmncn.flemingshop.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBasket
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlinx.coroutines.delay

@Composable
fun MainBottomBar(navController: NavController) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    val maxHeight = 64.dp
    var isInitialized by remember { mutableStateOf(false) }
    val isHome = currentRoute == "home"
    var isVisible by remember { mutableStateOf(!isHome) } // Visble/Oculta segun la ruta actual

    // Lanzar efecto solo al inicio para configurar correctamente el estado
    LaunchedEffect(currentRoute) {
        if (!isInitialized) {
            isInitialized = true // Marcar como inicializado después de evaluar la ruta inicial
        } else {
            if (isHome) {
                isVisible = false // Ocultar si estamos en Home
            } else {
                delay(300) // Retardar la visibilidad fuera de Home
                isVisible = true
            }
        }
    }

    // Altura condicional (instantánea) según `isVisible`
    val height = if (isVisible) maxHeight else 0.dp

    // Desplazamiento animado del BottomAppBar
    val animatedOffsetY by animateDpAsState(
        targetValue = if (isVisible) 0.dp else maxHeight,
        animationSpec = tween(durationMillis = 500), // Duración de la animación
        label = "BottomBarSlide"
    )

    // Evitar renderizar hasta que esté inicializado
    if (isInitialized) {
        Box(
            modifier = Modifier
                .height(height)
                .fillMaxWidth()
        ) {
            if (isVisible) {
                BottomAppBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset { IntOffset(0, animatedOffsetY.roundToPx()) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                ) {
                    // Home
                    NavigationBarItem(
                        selected = currentRoute == "home",
                        onClick = {
                            if (currentRoute != "home") {
                                navController.navigate("home") {
                                    popUpTo(navController.graph.startDestinationId)
                                    launchSingleTop = true
                                }
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = "Inicio",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        label = {
                            Text(
                                text = "Inicio",
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall,
                                maxLines = 1
                            )
                        }
                    )

                    // Familias
                    NavigationBarItem(
                        selected = currentRoute == "families",
                        onClick = {
                            if (currentRoute != "families") {
                                navController.navigate("families") {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.List,
                                contentDescription = "Familias",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        label = {
                            Text(
                                text = "Familias",
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall,
                                maxLines = 1
                            )
                        }
                    )

                    // Busqueda
                    NavigationBarItem(
                        selected = currentRoute == "search_articles",
                        onClick = {
                            if (currentRoute != "search_articles") {
                                navController.navigate("search_articles") {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Buscar",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        label = {
                            Text(
                                text = "Buscar",
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall,
                                maxLines = 1
                            )
                        }
                    )

                    // Lista de compra
                    NavigationBarItem(
                        selected = currentRoute == "shopping_list",
                        onClick = {
                            if (currentRoute != "shopping_list") {
                                navController.navigate("shopping_list") {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.ShoppingBasket,
                                contentDescription = "Lista de compra",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        label = {
                            Text(
                                text = "Lista de compra",
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall,
                                maxLines = 1
                            )
                        }
                    )
                }
            }
        }
    }
}
