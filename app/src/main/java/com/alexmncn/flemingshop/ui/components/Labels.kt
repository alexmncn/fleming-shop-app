package com.alexmncn.flemingshop.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun StockLabel(stock: Int, modifier: Modifier) {
    Box(
        modifier = modifier
            .wrapContentSize()
            .shadow(4.dp, shape = RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White) // Fondo blanco
    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(top = 3.dp, bottom = 3.dp, start = 6.dp, end = 6.dp)
        ) {
            // Indicador redondo de stock
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .padding(2.dp)
                    .shadow(
                        elevation = 6.dp,
                        shape = CircleShape,
                        clip = false, // Para que la sombra se extienda más allá del círculo
                        spotColor = if (stock > 0) Color.Green else Color.Red
                    )
                    .background(
                        color = if (stock > 0) Color(0xff00bb00) else Color.Red,
                        shape = CircleShape
                    )
            )

            // Texto según el stock
            Text(
                text = if (stock > 0) "Disponible" else "Sin stock",
                color = Color.Black,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 3.dp)
            )
        }
    }
}

@Composable
fun FeaturedLabel(modifier: Modifier, navController: NavController) {
    Box(
        modifier = modifier
            .wrapContentSize()
            .shadow(6.dp, shape = RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFFFCC00)), // !!! PROVISIONAL !!!
    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(top = 3.dp, bottom = 3.dp, start = 6.dp, end = 6.dp)
                .clickable { navController.navigate("featured_articles") }
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Destacado",
                tint = Color.White,
                modifier = Modifier.size(10.dp) // Tamaño del ícono
            )

            // Texto según el stock
            Text(
                text = "Destacado",
                color = Color.White,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 3.dp)
            )
        }
    }
}

@Composable
fun HiddenLabel(modifier: Modifier) {
    Box(
        modifier = modifier
            .wrapContentSize()
            .shadow(6.dp, shape = RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .background(color = Color(0xff424242)), // !!! PROVISIONAL !!!
    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(top = 3.dp, bottom = 3.dp, start = 6.dp, end = 6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.VisibilityOff,
                contentDescription = "Oculto",
                tint = Color.White,
                modifier = Modifier.size(10.dp) // Tamaño del ícono
            )

            // Texto según el stock
            Text(
                text = "Oculto",
                color = Color.White,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 3.dp)
            )
        }
    }
}