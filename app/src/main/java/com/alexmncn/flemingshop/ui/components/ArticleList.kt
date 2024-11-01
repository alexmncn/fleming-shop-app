package com.alexmncn.flemingshop.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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


@Composable
fun ArticleList(articles: List<Article>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(articles) { article ->
            ArticleItem(article)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun ArticleItem(article: Article) {
    val imageUrl = Constans.IMAGES_URL + "articles/${article.codebar}.webp"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = article.detalle, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = article.codebar.toString(), style = MaterialTheme.typography.bodyMedium)

            // Usando Coil para cargar la imagen
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Imagen del artículo",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Crop
            )
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
    ArticleList(articles = sampleArticles)
}