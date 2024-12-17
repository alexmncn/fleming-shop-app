package com.alexmncn.flemingshop.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.alexmncn.flemingshop.data.model.Article

@Composable
fun ArticleCarousel(total: Int, articles: List<Article>, isLoading: Boolean = false, showMoreRoute: String, navController: NavController) {

    // Si hay articulos
    if (articles.isNotEmpty()) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Espacio inicial
            item{
                Spacer(modifier = Modifier.padding(horizontal = 1.dp))
            }

            // Articulos
            items(articles.size) { index ->
                ReducedArticleCard(
                    article = articles[index],
                    navController = navController
                )
            }

            // Ver más
            item{
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme .colorScheme.onPrimary),
                    elevation = CardDefaults.cardElevation(2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(120.dp)
                        .clickable {
                            navController.navigate(showMoreRoute)
                        }
                ) {
                    Box( // Alineamiento central del texto
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Ver más",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )

                            Spacer(modifier = Modifier.height(5.dp))

                            Text(
                                text = "Ver ${total - articles.size} más",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }
            }

            // Espacio final
            item{
                Spacer(modifier = Modifier.padding(horizontal = 1.dp))
            }
        }
    } else { // Si no hay articulos
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
        ){
            Column(
                modifier = Modifier
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isLoading) { // Si esta cargando
                    CircularProgressIndicator(
                        modifier = Modifier.size(40.dp),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 2.dp
                    )
                } else { // Si no esta cargando -> no hay resultados | Otro error
                    Icon(
                        imageVector = Icons.Default.SearchOff,
                        contentDescription = "Sin resultados",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = "No se han encontrado artículos", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}