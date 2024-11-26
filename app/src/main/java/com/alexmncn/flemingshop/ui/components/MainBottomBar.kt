package com.alexmncn.flemingshop.ui.components

import android.app.Activity
import android.content.Intent
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
import com.alexmncn.flemingshop.ui.main.CodebarScannerActivity
import com.alexmncn.flemingshop.ui.main.FeaturedArticlesActivity
import com.alexmncn.flemingshop.ui.main.HomeActivity
import com.alexmncn.flemingshop.ui.main.NewArticlesActivity

@Composable
fun MainBottomBar() {
    val context = LocalContext.current
    val currentActivity = (context as? Activity)?.javaClass?.simpleName // Actividad donde nos encontramos

    BottomAppBar (
        modifier = Modifier
            .height(60.dp),
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = Color.White,
        contentPadding = PaddingValues(vertical = 10.dp)
    ) {
        // Home
        NavigationBarItem(
            selected = false,
            onClick = {
                val intent = Intent(context, HomeActivity::class.java)
                context.startActivity(intent)
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
                    text = "Home",
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        )

        // Destacados
        NavigationBarItem(
            selected = currentActivity == FeaturedArticlesActivity::class.java.simpleName,
            onClick = {
                val intent = Intent(context, FeaturedArticlesActivity::class.java)
                context.startActivity(intent)
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
            selected = currentActivity == NewArticlesActivity::class.java.simpleName,
            onClick = {
                val intent = Intent(context, NewArticlesActivity::class.java)
                context.startActivity(intent)
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

        // Escaner
        NavigationBarItem(
            selected = currentActivity == CodebarScannerActivity::class.java.simpleName,
            onClick = {
                val intent = Intent(context, CodebarScannerActivity::class.java)
                context.startActivity(intent)
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.QrCodeScanner,
                    contentDescription = "Escaner de codigos de barras",
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