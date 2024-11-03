package com.alexmncn.flemingshop.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.alexmncn.flemingshop.data.model.Article
import com.alexmncn.flemingshop.utils.Constans
import java.math.BigInteger
import java.util.Date
import com.alexmncn.flemingshop.utils.capitalizeText


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleList(articles: List<Article>, listName: String) {
    Column (
        modifier = Modifier
            .padding(top = 10.dp, bottom = 10.dp)
    ) {
        Text(text = listName, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(10.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(4.dp), // 14dp -10dp from card safe zone
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(articles.size) { index ->
                ArticleCard(article = articles[index])

            }
        }
    }
}

@Composable
fun ArticleCard(article: Article) {
    val imageUrl = Constans.IMAGES_URL + "articles/${article.codebar}.webp"

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .padding(top = 2.dp, bottom = 10.dp) // Safe zone for card shadow
            .fillMaxWidth(),
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
            Text(text = capitalizeText(article.detalle), style = MaterialTheme.typography.bodyLarge)
            Text(text = "Ref: " + article.ref.toString(), style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            Text(text = "Stock: " + article.stock.toString(), style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            Text(text = article.pvp.toString() + " €", style = MaterialTheme.typography.titleSmall, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, modifier = Modifier.padding(top = 6.dp, bottom = 2.dp))
        }
    }
}

@Preview
@Composable
fun PreviewArticleList() {
    val sampleArticles = listOf(
        Article(
            ref = BigInteger("1000000001"),
            codebar = BigInteger("1"),
            detalle = "Artículo de prueba A",
            codfam = 1,
            pcosto = 10.50f,
            pvp = 15.75f,
            stock = 100,
            factualizacion = Date(),
            destacado = true,
            hidden = false
        ),
        Article(
            ref = BigInteger("1000000002"),
            codebar = BigInteger("2"),
            detalle = "Artículo de prueba B",
            codfam = 2,
            pcosto = 20.00f,
            pvp = 30.00f,
            stock = 50,
            factualizacion = Date(),
            destacado = false,
            hidden = false
        ),
        Article(
            ref = BigInteger("1000000003"),
            codebar = BigInteger("3"),
            detalle = "Artículo de prueba C",
            codfam = 3,
            pcosto = 5.00f,
            pvp = 7.50f,
            stock = 200,
            factualizacion = Date(),
            destacado = true,
            hidden = true
        ),
        Article(
            ref = BigInteger("1000000004"),
            codebar = BigInteger("4"),
            detalle = "Artículo de prueba D",
            codfam = 4,
            pcosto = 12.25f,
            pvp = 18.99f,
            stock = 25,
            factualizacion = Date(),
            destacado = false,
            hidden = false
        ),
        Article(
            ref = BigInteger("1000000005"),
            codebar = BigInteger("5"),
            detalle = "Artículo de prueba E",
            codfam = 5,
            pcosto = 8.75f,
            pvp = 13.50f,
            stock = 75,
            factualizacion = Date(),
            destacado = true,
            hidden = false
        )
    )
    ArticleList(sampleArticles, "Destacado")
}