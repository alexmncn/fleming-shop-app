package com.alexmncn.flemingshop.ui.screens

import android.util.Log
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.NewReleases
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingBasket
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.alexmncn.flemingshop.data.model.Article
import com.alexmncn.flemingshop.data.model.Family
import com.alexmncn.flemingshop.data.network.ApiClient
import com.alexmncn.flemingshop.data.network.ApiService
import com.alexmncn.flemingshop.data.repository.CatalogRepository
import com.alexmncn.flemingshop.ui.components.ArticleCarousel
import com.alexmncn.flemingshop.ui.components.FamilyCarousel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Composable
fun HomeScreen(navController: NavController) {
    val configuration = LocalConfiguration.current
    val context = LocalContext.current
    val apiClient = ApiClient.provideOkHttpClient(context)
    val catalogRepository: CatalogRepository by lazy { CatalogRepository(ApiService(apiClient)) }
    val collapsibleGreetingViewModel: CollapsibleGreetingViewModel = viewModel() // Instancia del viewmodel
    val isCollapsed by collapsibleGreetingViewModel.isCollapsed // Estado de si está colapsado o no
    var offsetY by remember { mutableFloatStateOf(0f) } // Desplazamiento temporal durante el gesto
    val screenHeight = configuration.screenHeightDp.dp
    val initialHeight = screenHeight
    val targetHeight = 50.dp
    val scrollState = rememberScrollState()

    // Altura animada al final del gesto
    val animatedHeight by animateDpAsState(
        targetValue = if (isCollapsed) targetHeight else initialHeight,
        animationSpec = tween(durationMillis = 500),
        label = "animatedHeight"
    )

    // Animación flotante
    val offsetY2 by rememberInfiniteTransition(label = "floating").animateFloat(
        initialValue = 0f,
        targetValue = 10f, // La distancia que se mueve hacia arriba y hacia abajo
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000, // Duración
                easing = EaseOut
            ),
            repeatMode = RepeatMode.Reverse // Movimiento de ida y vuelta
        ), label = ""
    )

    // Aticulos/Familias carousels
    var limit = 10;

    // Destacados
    var featuredArticlesTotal by remember { mutableIntStateOf(0) }
    var featuredArticles by remember { mutableStateOf<List<Article>>(emptyList()) }
    var isLoadingFeaturedArticles by remember { mutableStateOf(false) }

    fun loadFeaturedArticles() {
        CoroutineScope(Dispatchers.IO).launch {
            isLoadingFeaturedArticles = true

            try {
                featuredArticlesTotal = catalogRepository.getFeaturedArticlesTotal()
                featuredArticles = catalogRepository.getFeaturedArticles(perPage = limit)
            } catch (e: Exception) {
                Log.e("error", e.toString())
            }

            isLoadingFeaturedArticles = false
        }
    }

    // Novedades
    var newArticlesTotal by remember { mutableIntStateOf(0) }
    var newArticles by remember { mutableStateOf<List<Article>>(emptyList()) }
    var isLoadingNewArticles by remember { mutableStateOf(false) }

    fun loadNewArticles() {
        CoroutineScope(Dispatchers.IO).launch {
            isLoadingNewArticles = true

            try {
                newArticlesTotal = catalogRepository.getNewArticlesTotal()
                newArticles = catalogRepository.getNewArticles(perPage = limit)
            } catch (e: Exception) {
                Log.e("error", e.toString())
            }

            isLoadingNewArticles = false
        }
    }

    // Familias
    var families by remember { mutableStateOf<List<Family>>(emptyList()) }
    var isLoadingFamilies by remember { mutableStateOf(false) }
    val limitFamilies = 15;

    fun loadFamilies() {
        CoroutineScope(Dispatchers.IO).launch {
            isLoadingFamilies = true

            try {
                families = catalogRepository.getFamilies()
            } catch (e: Exception) {
                Log.e("error", e.toString())
            }

            isLoadingFamilies = false
        }
    }


    LaunchedEffect(Unit) {
        loadFeaturedArticles()
        loadNewArticles()
        loadFamilies()
    }


    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Bienvenida
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (isCollapsed) animatedHeight else initialHeight) // Usamos el valor animado para la altura/ Controlamos cuando se carga ya colapsado
                .background(MaterialTheme.colorScheme.primary)
                .pointerInput(Unit) {
                    detectVerticalDragGestures(
                        onDragEnd = {
                            if (offsetY < -(screenHeight.value - targetHeight.value) / 4) {
                                // Si se desliza suficiente hacia arriba, colapsar
                                collapsibleGreetingViewModel.collapse()
                                offsetY = 0f
                            } else {
                                // Si no, volver al estado original
                                offsetY = 0f
                            }
                        },
                        onDragCancel = {
                            offsetY = 0f // Resetear el desplazamiento si se cancela el gesto
                        },
                        onVerticalDrag = { _, dragAmount ->
                            offsetY += dragAmount
                        }
                    )
                }
        ) {
            // Texto de bienvenida
            Column (
                modifier = Modifier
                    .align(if (isCollapsed && animatedHeight < 300.dp) Alignment.CenterStart else Alignment.Center)
                    .padding(horizontal = 15.dp),
                Arrangement.spacedBy(5.dp)
            ) {
                if (isCollapsed && animatedHeight < 300.dp) {
                    // Linea 1
                    Text(
                        text = "Catálogo",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium
                    )
                } else {
                    // Linea 1
                    Text(
                        text = "Bienvenido/a a",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                    )

                    // Linea 3
                    Text(
                        text = "Tienda Fleming",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 30.sp),
                    )
                }
            }

            // Indicador para deslizar hacia arriba cuando está expandido
            if (!isCollapsed) {
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 30.dp)
                        .fillMaxWidth()
                        .offset(y = offsetY2.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = "Explorar",
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                    Text(
                        text = "Desliza para explorar",
                        color = Color.White,
                        style = MaterialTheme.typography.titleSmall,
                    )
                }
            }
        }


        // Options
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .padding(horizontal = 10.dp)
                .fillMaxSize(),
        ) {
            Spacer(modifier = Modifier.height(15.dp))

            // Featured Articles
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, bottom = 10.dp, start = 13.dp, end = 13.dp)
                        .clickable { navController.navigate("featured_articles") },
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 30.dp),
                    ) {
                        // Titulo
                        Text(text = "Destacados", style = MaterialTheme.typography.titleSmall)
                        // Descripción
                        Text(
                            text = "En esta sección puedes encontrar artículos seleccionados que te pueden interesar",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Icon(
                        imageVector = Icons.Outlined.StarBorder,
                        contentDescription = "Destacado",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(30.dp)
                            .weight(0.2f)
                    )
                }

                // Tira de articulos
                ArticleCarousel(
                    total = featuredArticlesTotal,
                    articles = featuredArticles,
                    isLoading = isLoadingFeaturedArticles,
                    showMoreRoute = "featured_articles",
                    navController = navController
                )

                Spacer(modifier = Modifier.height(10.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))

            // New Articles
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, bottom = 10.dp, start = 13.dp, end = 13.dp)
                        .clickable { navController.navigate("new_articles") },
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 30.dp),
                    ) {
                        // Titulo
                        Text(text = "Novedades", style = MaterialTheme.typography.titleSmall)
                        // Descripción
                        Text(
                            text = "En esta sección puedes encontrar articulos que han llegado recientemente a la tienda",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Icon(
                        imageVector = Icons.Outlined.NewReleases,
                        contentDescription = "Novedades",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(30.dp)
                            .weight(0.2f)
                    )
                }

                // Tira de articulos
                ArticleCarousel(
                    total = newArticlesTotal,
                    articles =  newArticles,
                    isLoading = isLoadingNewArticles,
                    showMoreRoute = "new_articles",
                    navController = navController
                )

                Spacer(modifier = Modifier.height(10.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Families
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, bottom = 10.dp, start = 13.dp, end = 13.dp)
                        .clickable { navController.navigate("families") },
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 50.dp),
                    ) {
                        // Titulo
                        Text(text = "Familias", style = MaterialTheme.typography.titleSmall)
                        // Descripción
                        Text(text = "En esta sección puedes encontrar articulos agrupados por familias", style = MaterialTheme.typography.bodyMedium)
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.List,
                        contentDescription = "Familias",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(30.dp)
                            .weight(0.2f)
                    )
                }

                // Tira de familias
                FamilyCarousel(
                    families = families.shuffled().take(limitFamilies),
                    isLoading = isLoadingFamilies,
                    navController = navController
                )

                Spacer(modifier = Modifier.height(10.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Search Articles
            Card (
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate("search_articles") },
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, bottom = 10.dp, start = 13.dp, end = 13.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 50.dp),
                    ) {
                        // Titulo
                        Text(text = "Buscar articulos", style = MaterialTheme.typography.titleSmall)
                        // Descripción
                        Text(text = "En esta sección puedes buscar entre mas de 8000 articulos por su descripción", style = MaterialTheme.typography.bodyMedium, overflow = TextOverflow.Clip)
                    }

                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = "Buscar",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(30.dp)
                            .weight(0.2f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Lista Compra
            Card (
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate("shopping_list") },
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
               Row(
                   verticalAlignment = Alignment.CenterVertically,
                   horizontalArrangement = Arrangement.SpaceBetween,
                   modifier = Modifier
                       .fillMaxWidth()
                       .padding(top = 10.dp, bottom = 10.dp, start = 13.dp, end = 13.dp)
               ) {
                   Column(
                       modifier = Modifier
                           .weight(1f)
                           .padding(end = 50.dp)
                   ) {
                       // Titulo
                       Text(text = "Lista de compra", style = MaterialTheme.typography.titleSmall)
                       // Descripción
                       Text(
                           text = "Aquí puedes ver los artículos que has agregado a tu lista de compra",
                           style = MaterialTheme.typography.bodyMedium,
                           overflow = TextOverflow.Clip
                       )
                   }

                   Icon(
                       imageVector = Icons.Outlined.ShoppingBasket,
                       contentDescription = "Lista de compra",
                       tint = MaterialTheme.colorScheme.primary,
                       modifier = Modifier
                           .size(30.dp)
                           .weight(0.2f)
                   )
               }
            }

            Spacer(modifier = Modifier.height(15.dp))
        }
    }
}

// Modelo para el estado del mensaje de bienvenida (para mantener en la sesión)
class CollapsibleGreetingViewModel : ViewModel() {
    // El estado se mantiene durante toda la sesión
    private val _isCollapsed = mutableStateOf(false)
    val isCollapsed: State<Boolean> = _isCollapsed

    fun collapse() {
        _isCollapsed.value = true
    }
}
