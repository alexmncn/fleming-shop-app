package com.alexmncn.flemingshop.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.alexmncn.flemingshop.data.model.Article
import kotlinx.coroutines.launch

@Composable
fun ArticleList(total: Int, articles: List<Article>, listName: String, onShowMore: () -> Unit, navController: NavController) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp  // Altura de la pantalla
    val scrollState = rememberScrollState() // Scroll principal
    val gridState = rememberLazyGridState() // Scroll del grid de articulos
    val coroutineScope = rememberCoroutineScope()
    val gridColumns = 2 // Numero de columnas en el grid de articulos

    // Progreso de la barra que muestra la cantidad de artículos cargados
    val articlesLoadedProgress = articles.size.toFloat() / total.toFloat()

    // Guardamos el tamaño anterior de la lista de articulos
    var previousArticleCount by remember { mutableIntStateOf(articles.size) }
    val isLoading = remember { mutableStateOf(false) }

    //Comparamos el tamaño de la lista de articulos con el tamaño anterior para determinar
    // si se ha terminado de cargar articulos
    LaunchedEffect(articles.size) {
        if (articles.size > previousArticleCount) {
            isLoading.value = false
            previousArticleCount = articles.size
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
        ) {
            // Padding
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                Arrangement.SpaceBetween,
                Alignment.CenterVertically
            ) {
                // Encabezado
                Text(text = listName, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onPrimary)
                Text(text = "$total artículos", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimary)
            }
        }

        if (articles.isNotEmpty()) {
            // Articles
            Column(
                modifier = Modifier
                    .padding(horizontal = 10.dp)  // Margen vertical para todos los elementos sin dejar huecos en el desp.
            ) {
                Spacer(modifier = Modifier.height(10.dp))

                // Artículos en cuadricula
                LazyVerticalGrid(
                    columns = GridCells.Fixed(gridColumns),
                    state = gridState,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.heightIn(max = screenHeight), // Limita el alto del grid para que el scroll no cause errores
                ) {
                    items(articles.size) { index ->
                        ArticleCard(
                            article = articles[index],
                            navController = navController
                        )
                    }
                }

                // Barra progreso y boton subir
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                ) {
                    // Contenedor para alineamiento
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxSize(),
                    ){
                        // Boton para volver arriba
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    gridState.animateScrollToItem(0)
                                    scrollState.animateScrollTo(0)
                                }
                            },
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.primary , CircleShape)
                                .size(35.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowUpward,
                                contentDescription = "Subir",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }

                    // Barra de progreso
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .align(Alignment.Center),
                    ) {
                        Text(
                            text = "${articles.size} de $total artículos",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(modifier = Modifier.height(5.dp))

                        LinearProgressIndicator(
                            progress = { articlesLoadedProgress },
                            modifier = Modifier
                                .width(200.dp)
                                .height(4.dp)
                                .clip(RoundedCornerShape(10.dp))
                        )
                    }
                }

                // Botón "Mostrar más"
                if (articles.size < total) {
                    Card(
                        onClick = {
                            isLoading.value = true
                            onShowMore()
                        },
                        modifier = Modifier
                            .fillMaxWidth(),
                        enabled = !isLoading.value, // Deshabilita el boton si se estan cargango artículos
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(10.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally

                        ) {
                            if (isLoading.value) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("Mostrar más", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
                            }
                        }
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
            ){
                Column(
                    modifier = Modifier
                        .align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
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