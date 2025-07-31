package com.alexmncn.flemingshop.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.alexmncn.flemingshop.data.db.AppDatabase
import com.alexmncn.flemingshop.data.db.ArticleItem
import com.alexmncn.flemingshop.data.repository.ShoppingListRepository
import com.alexmncn.flemingshop.data.viewmodel.ShoppingListViewModel
import com.alexmncn.flemingshop.data.viewmodel.ShoppingListViewModelFactory
import com.alexmncn.flemingshop.ui.theme.Blue50
import com.alexmncn.flemingshop.utils.capitalizeText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun ShoppingListScreen(db: AppDatabase, navController: NavController) {
    val shoppingListRepository: ShoppingListRepository by lazy { ShoppingListRepository(db) }
    val shoppingListViewModel: ShoppingListViewModel = viewModel(factory = ShoppingListViewModelFactory(shoppingListRepository))
    val shoppingList = shoppingListViewModel.articleItems.collectAsState()
    val finalPrize = shoppingListViewModel.getFinalPrice().collectAsState(initial = 0.0)
    var editMode by remember { mutableStateOf(false) }
    val selectedArticleItems = remember { mutableStateMapOf<String, ArticleItem>() }
    var removedArticlesVisible by remember { mutableStateOf(true) }
    val deleteAnimationTime = 500

    fun editModeToggle() {
        // Si estamos confirmando cambios
        if (editMode) {
            // Si hay articulos seleccionados, los eliminamos
            if (selectedArticleItems.isNotEmpty()) {
                CoroutineScope(Dispatchers.IO).launch {
                    // Se ocultan
                    removedArticlesVisible = false

                    delay(deleteAnimationTime.toLong()+100) // Delay para la animación de eliminar se complete

                    withContext(Dispatchers.Main) {
                        // Se eliminan
                        selectedArticleItems.forEach { (_, article) ->
                            shoppingListViewModel.deleteArticleByCodebar(article.codebar)
                        }
                    }

                    selectedArticleItems.clear() // Reinicio de map

                    removedArticlesVisible = true // Reinicio de estado
                }
            }
        }
        editMode = !editMode // Cambio de estado
    }

    fun onSelectArticleItem(selected: Boolean, article: ArticleItem) {
        if (!selected) { // Actuamos con el estado anterior
            selectedArticleItems[article.codebar] = article // Añadimos a selecc.
        } else {
            selectedArticleItems.remove(article.codebar) // Eliminados de selecc.
        }
    }


    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize(),
    ) {
        Card(
            elevation = CardDefaults.cardElevation(6.dp),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                // Title
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primary)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 15.dp, vertical = 10.dp),
                        Arrangement.SpaceBetween,
                        Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Lista de la compra",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onPrimary
                        )

                        if (shoppingList.value.isNotEmpty()) {
                            // Editar
                            Icon(
                                imageVector = if (editMode) Icons.Outlined.Check else Icons.Default.Edit,
                                contentDescription = if (editMode) "Confirmar cambios" else "Editar lista",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier
                                    .size(20.dp)
                                    .clickable { editModeToggle() }
                            )
                        }
                    }
                }

                if (shoppingList.value.isNotEmpty()) {
                    // Index
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Blue50)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp, horizontal = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "Cantidad",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.End,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1,
                                modifier = Modifier
                                    .weight(0.15f)
                            )

                            Text(
                                text = "Detalle",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Start,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1,
                                modifier = Modifier
                                    .weight(0.70f)
                                    .padding(start = 10.dp)
                            )

                            Text(
                                text = "Precio",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                overflow =  TextOverflow.Ellipsis,
                                maxLines = 1,
                                modifier = Modifier
                                    .weight(0.15f)
                                    .padding(start = 10.dp)
                            )
                        }
                    }

                    // Lista de articulos
                    LazyColumn(
                        state = rememberLazyListState(),
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
                    ) {
                        items(shoppingList.value.size) { index ->
                            val item = shoppingList.value[index]

                            // Animación de visibilidad
                            AnimatedVisibility(
                                // Visible si no se está seleccionado, ni oculto.
                                visible = !selectedArticleItems.containsKey(item.codebar) || removedArticlesVisible,
                                // Animación de delizar derecha
                                exit = fadeOut(animationSpec = tween(deleteAnimationTime)) + slideOutHorizontally(animationSpec = tween(deleteAnimationTime), targetOffsetX = { it }),
                            ) {
                                ShoppingListItem(
                                    articleItem = item,
                                    editMode = editMode,
                                    isChecked = selectedArticleItems.containsKey(item.codebar),
                                    onSelectionChanged = { selected, article -> onSelectArticleItem(selected, article) },
                                    onClick = { codebar -> navController.navigate("article_detail/$codebar") }
                                )
                            }

                            HorizontalDivider(color = Color(0xFFE9E9E9), thickness = 1.dp) // !!!! PROVISIONAL !!!!
                        }
                    }
                } else { // Si no hay articulos
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        // Texto central
                        Text(
                            text = "No hay artículos en la lista",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray,
                            fontWeight = FontWeight.Bold
                        )

                        // Botón esquina
                        Box(
                            contentAlignment = Alignment.BottomEnd,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(10.dp)
                        ) {
                            // Add Button
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                                elevation = CardDefaults.cardElevation(3.dp),
                                modifier = Modifier
                                    .clickable {
                                        // Navegar a pagina busqueda
                                        navController.navigate("search_articles") {
                                            popUpTo(navController.graph.startDestinationId) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .padding(7.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Add,
                                        contentDescription = "Añadir",
                                        tint = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.size(22.dp)
                                    )

                                    Text(
                                        text = "Añadir",
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.padding(start = 5.dp, end = 3.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Total
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                ) {
                    HorizontalDivider(color = Color.LightGray, thickness = 1.dp) // !!!! PROVISIONAL !!!!
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 15.dp, horizontal = 30.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Total",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Start,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                        )

                        Text(
                            text = finalPrize.value.toString() + " €",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            overflow =  TextOverflow.Ellipsis,
                            maxLines = 1,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ShoppingListItem(articleItem: ArticleItem, editMode: Boolean = false, isChecked: Boolean = false, onClick: (codebar: String) -> Unit, onSelectionChanged: (Boolean, ArticleItem) -> Unit) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                if (editMode) {
                    onSelectionChanged(isChecked, articleItem)
                } else {
                    onClick(articleItem.codebar)
                }
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 15.dp, horizontal = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (editMode) {
                Checkbox(
                    checked = isChecked,
                    onCheckedChange = {
                        onSelectionChanged(isChecked, articleItem)
                    },
                    modifier = Modifier
                        .size(10.dp)
                        .weight(0.15f)
                        .padding(end = 15.dp),
                )

            } else {
                Text(
                    text = articleItem.quantity.toString(),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.End,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(0.15f)
                        .padding(end = 15.dp)
                )
            }

            Text(
                text = capitalizeText(articleItem.detalle),
                style = MaterialTheme.typography.bodyLarge,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(0.70f)
                    .padding(start = 10.dp)
            )

            Text(
                text = articleItem.pvp.toString(),
                style = MaterialTheme.typography.bodyLarge,
                overflow =  TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(0.15f)
                    .padding(start = 10.dp)
            )
        }
    }
}