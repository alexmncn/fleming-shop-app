package com.alexmncn.flemingshop.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.alexmncn.flemingshop.data.model.Article
import com.alexmncn.flemingshop.utils.Constans
import com.alexmncn.flemingshop.utils.capitalizeText

@Composable
fun ArticleCard(article: Article, navController: NavController) {
    val imageUrl = Constans.IMAGES_URL + "articles/${article.codebar}.webp"

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .padding(top = 2.dp, bottom = 10.dp) // Safe zone for card shadow
            .fillMaxWidth()
            .clickable {
                navController.navigate("article_detail/${article.codebar}")
            },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        // Usamos un Box para superponer elementos
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .size(140.dp)
        ) {
            // Imagen
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Imagen del artículo",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Etiqueta flotante de Stock
            StockLabel(
                article.stock,
                modifier = Modifier
                    .padding(5.dp)
                    .align(Alignment.TopEnd) // Lo posicionamos en la esquina derecha
            )

            if (article.hidden) {
                // Icono flotante de Oculto (si lo está el artículo)
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .align(Alignment.TopStart)
                        .padding(6.dp)
                        .background(
                            color = Color(0xff424242), // !!! PROVISIONAL !!!
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center // Centrar el ícono dentro del fondo
                ) {
                    Icon(
                        imageVector = Icons.Default.VisibilityOff,
                        contentDescription = "Favorito",
                        tint = Color.White,
                        modifier = Modifier.size(15.dp) // Tamaño del ícono
                    )
                }
            }
        }

        // Detalles del artículo
        Column(
            modifier = Modifier
                .background(Color(0xfff5f5f5)) // !!! PROVISIONAL !!!
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
        ) {
            Text(text = capitalizeText(article.detalle), style = MaterialTheme.typography.bodyLarge, color = Color.Black)
            Text(text = "Ref: " + article.ref.toString(), style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            Text(
                text = article.pvp.toString() + " €",
                style = MaterialTheme.typography.titleSmall,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(top = 6.dp, bottom = 2.dp)
            )
        }
    }
}
