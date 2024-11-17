package com.alexmncn.flemingshop.ui.screens.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.alexmncn.flemingshop.data.model.Article
import com.alexmncn.flemingshop.ui.components.ArticleList
import com.alexmncn.flemingshop.ui.components.MainTopBar
import com.alexmncn.flemingshop.ui.theme.FlemingShopTheme
import com.alexmncn.flemingshop.utils.Constans
import com.alexmncn.flemingshop.utils.capitalizeText
import java.math.BigInteger
import java.util.Date

@Composable
fun ArticleScreen(article: Article) {
    val imageUrl = Constans.IMAGES_URL + "articles/${article.codebar}.webp"

    FlemingShopTheme {
        Scaffold(
            topBar = { MainTopBar() },
            content = { paddingValues ->
                Column(
                    modifier = Modifier
                        .padding(paddingValues) // Safe zone from topbar
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Imagen del artículo",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentScale = ContentScale.FillWidth
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                    ) {
                        Text(text = capitalizeText(article.detalle), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Normal, color = Color.Black)
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(text = "Ref: " + article.ref.toString(), style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = "Cod: " + article.codebar.toString(), style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = "Stock: " + article.stock.toString(), style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = article.pvp.toString() + " €",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .padding(top = 6.dp, bottom = 2.dp)
                        )
                    }
                }
            },
        )
    }
}

@Preview
@Composable
fun PreviewArticle() {
        val article = Article(
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
        )
    ArticleScreen(article = article)
}