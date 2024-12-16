package com.alexmncn.flemingshop.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.RootGroupName
import androidx.compose.ui.platform.LocalConfiguration
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
import com.alexmncn.flemingshop.ui.theme.Blue100
import com.alexmncn.flemingshop.ui.theme.Blue200
import com.alexmncn.flemingshop.ui.theme.Blue50
import com.alexmncn.flemingshop.utils.capitalizeText

@Composable
fun ShoppingListScreen(db: AppDatabase, navController: NavController) {
    val shoppingListRepository: ShoppingListRepository by lazy { ShoppingListRepository(db) }
    val shoppingListViewModel: ShoppingListViewModel = viewModel(factory = ShoppingListViewModelFactory(shoppingListRepository))
    val shoppingList = shoppingListViewModel.articleItems.collectAsState()
    val finalPrize = shoppingListViewModel.getFinalPrice().collectAsState(initial = 0.0)
    var editMode by remember { mutableStateOf(false) }

    fun editModeToggle() {
        editMode = !editMode
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

                        if (editMode) {
                            Icon(
                                imageVector = Icons.Outlined.Check,
                                contentDescription = "Confirmar cambios",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier
                                    .size(20.dp)
                                    .clickable { editModeToggle() }
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Editar lista",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier
                                    .size(20.dp)
                                    .clickable { editModeToggle() }
                            )
                        }
                    }
                }

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
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    items(shoppingList.value.size) { index ->
                        val item = shoppingList.value[index]
                        ShoppingListItem(item, editMode = editMode, onClick = { codebar -> navController.navigate("article_detail/$codebar") })
                        HorizontalDivider(color = Color(0xFFE9E9E9), thickness = 1.dp) // !!!! PROVISIONAL !!!!
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
fun ShoppingListItem(articleItem: ArticleItem, editMode: Boolean = false, onClick: (codebar: Long) -> Unit) {
    var isChecked by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { if (editMode) isChecked = !isChecked else onClick(articleItem.codebar) }
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
                    onCheckedChange = { isChecked = !isChecked },
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