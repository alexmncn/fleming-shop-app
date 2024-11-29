package com.alexmncn.flemingshop.ui.screens.shared

import android.util.Log
import android.widget.LinearLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.outlined.FileUpload
import androidx.compose.material.icons.outlined.Upload
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.PathNode
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.alexmncn.flemingshop.data.model.Article
import com.alexmncn.flemingshop.data.network.ApiClient
import com.alexmncn.flemingshop.data.network.ApiService
import com.alexmncn.flemingshop.data.network.AuthManager
import com.alexmncn.flemingshop.data.repository.ArticleRepository
import com.alexmncn.flemingshop.ui.components.StockLabel
import com.alexmncn.flemingshop.utils.Constans
import com.alexmncn.flemingshop.utils.capitalizeText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun DetailArticleScreen(codebar: String) {
    val context = LocalContext.current
    val apiClient = ApiClient.provideOkHttpClient(context)
    val articleRepository: ArticleRepository by lazy { ArticleRepository(ApiService(apiClient)) }

    var article by remember { mutableStateOf<Article?>(null) }
    val imageUrl = Constans.IMAGES_URL + "articles/${article?.codebar}.webp"

    // Funcion para cargar articulos, por pagina
    fun loadFeaturedArticles() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                article = articleRepository.getSearchArticles(search = codebar, filter = "codebar")[0]
            } catch (e: Exception) {
                Log.e("error", e.toString())
            }
        }
    }

    // Llama a la función de actualización cuando se crea la actividad
    LaunchedEffect(Unit) {
        loadFeaturedArticles()
    }

    article?.let {
        Column (
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .background(Color.White)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Imagen del artículo",
                modifier = Modifier
                    .fillMaxWidth(),
                contentScale = ContentScale.FillWidth
            )

            // Detalles del articulo
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
            ) {
                Text(
                    text = capitalizeText(article?.detalle),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Normal,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = "Ref: " + article?.ref.toString(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "Cod: " + article?.codebar.toString(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(2.dp))

                if (AuthManager.isAuthenticated()) {
                    Text(
                        text = "Stock: " + article?.stock.toString(),
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .padding(top = 6.dp, bottom = 2.dp)
                        .align(Alignment.End)
                ) {
                    Text(
                        text = article?.pvp.toString() + " €",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                    )
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.End)
                ) {
                    // Etiqueta de Stock
                    StockLabel(article!!.stock, modifier = Modifier.shadow(2.dp, shape = RoundedCornerShape(20.dp)))
                }
            }

            // Admin Section
            if (AuthManager.isAuthenticated()) {
                HorizontalDivider(color = Color.LightGray, thickness = 1.dp)

                Column(
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    // Titulo
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Acciones",
                            tint = Color.Black
                        )
                        Text(
                            text = "Acciones",
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier
                                .padding(start = 5.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(5.dp))

                    // Opciones
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp, bottom = 10.dp)
                                .shadow(4.dp, shape = RoundedCornerShape(3.dp))
                                .clip(RoundedCornerShape(3.dp))
                                .background(Color.White)
                                .clickable {  }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (article!!.destacado) "Destacado" else "Destacar",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Normal,
                                )

                                Checkbox(checked = article!!.destacado, onCheckedChange = {}, modifier = Modifier.size(20.dp))
                            }
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp, bottom = 10.dp)
                                .shadow(4.dp, shape = RoundedCornerShape(3.dp))
                                .clip(RoundedCornerShape(3.dp))
                                .background(Color.White)
                                .clickable {  }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (article!!.hidden) "Oculto" else "Ocultar",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Normal,
                                )

                                Checkbox(checked = article!!.hidden, onCheckedChange = {}, modifier = Modifier.size(20.dp))
                            }
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp, bottom = 10.dp)
                                .shadow(4.dp, shape = RoundedCornerShape(3.dp))
                                .clip(RoundedCornerShape(3.dp))
                                .background(Color.White)
                                .clickable {  }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Subir imagen",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Normal,
                                )

                                Icon(
                                    imageVector = Icons.Outlined.FileUpload,
                                    contentDescription = "Subir imagen",
                                    tint = Color.Black
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}