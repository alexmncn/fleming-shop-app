package com.alexmncn.flemingshop.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.FiberNew
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun MainBottomBar(navController: NavController) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    BottomAppBar(
        modifier = Modifier.height(60.dp),
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = Color.White,
        contentPadding = PaddingValues(vertical = 10.dp)
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
                    tint = Color.White
                )
            },
            label = {
                Text(
                    text = "Inicio",
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        )

        // Destacados
        NavigationBarItem(
            selected = currentRoute == "featured_articles",
            onClick = {
                if (currentRoute != "featured_articles") {
                    navController.navigate("featured_articles") {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Articulos Destacados",
                    tint = Color.White
                )
            },
            label = {
                Text(
                    text = "Destacados",
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        )

        // Novedades
        NavigationBarItem(
            selected = currentRoute == "new_articles",
            onClick = {
                if (currentRoute != "new_articles") {
                    navController.navigate("new_articles") {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.FiberNew,
                    contentDescription = "Novedades",
                    tint = Color.White
                )
            },
            label = {
                Text(
                    text = "Novedades",
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        )

        // Familias
        NavigationBarItem(
            selected = currentRoute == "families",
            onClick = {
                if (currentRoute != "families") {
                    navController.navigate("families") {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.List,
                    contentDescription = "Familias",
                    tint = Color.White
                )
            },
            label = {
                Text(
                    text = "Familias",
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        )

        // Busqueda
        NavigationBarItem(
            selected = currentRoute == "search_articles",
            onClick = {
                if (currentRoute != "search_articles") {
                    navController.navigate("search_articles") {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Búsqueda",
                    tint = Color.White
                )
            },
            label = {
                Text(
                    text = "Búsqueda",
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        )
    }
}
