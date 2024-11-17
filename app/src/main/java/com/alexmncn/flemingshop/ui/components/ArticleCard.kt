package com.alexmncn.flemingshop.ui.components

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.alexmncn.flemingshop.data.model.Article
import com.alexmncn.flemingshop.ui.main.DetailArticleActivity
import com.alexmncn.flemingshop.utils.Constans
import com.alexmncn.flemingshop.utils.capitalizeText

@Composable
fun ArticleCard(article: Article) {
    val context = LocalContext.current
    val imageUrl = Constans.IMAGES_URL + "articles/${article.codebar}.webp"

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .padding(top = 2.dp, bottom = 10.dp) // Safe zone for card shadow
            .fillMaxWidth()
            .clickable {
                val intent = Intent(context, DetailArticleActivity::class.java).apply {
                    putExtra("codebar", article.codebar.toString())
                }
                context.startActivity(intent)
            },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        // Usando Coil para cargar la imagen
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = "Imagen del artículo",
            modifier = Modifier
                .fillMaxWidth()
                .size(120.dp),
            contentScale = ContentScale.FillHeight
        )

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