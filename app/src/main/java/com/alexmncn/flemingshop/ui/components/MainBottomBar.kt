package com.alexmncn.flemingshop.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FiberNew
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.QrCodeScanner
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

        // Escáner
        NavigationBarItem(
            selected = currentRoute == "barcode_scanner",
            onClick = {
                if (currentRoute != "barcode_scanner") {
                    navController.navigate("barcode_scanner") {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.QrCodeScanner,
                    contentDescription = "Escaner de códigos de barras",
                    tint = Color.White
                )
            },
            label = {
                Text(
                    text = "Escaner",
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        )
    }
}
