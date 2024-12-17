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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
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
import com.alexmncn.flemingshop.data.model.Family

@Composable
fun FamilyCarousel(families: List<Family>, isLoading: Boolean, navController: NavController) {
    // Si hay articulos
    if (families.isNotEmpty()) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Espacio inicial
            item{
                Spacer(modifier = Modifier.padding(horizontal = 1.dp))
            }

            // Familias
            items(families.size) { index ->
                VariantFamilyCard(
                    family = families[index],
                    onShowFamily = { codfam, nomfam ->
                        navController.navigate("families/family_articles/$codfam/$nomfam")
                    }
                )
            }

            // Ver más
            item{
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme .colorScheme.onPrimary),
                    elevation = CardDefaults.cardElevation(2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(35.dp)
                        .width(100.dp)
                        .clickable {
                            navController.navigate("families")
                        }
                ) {
                    Box( // Alineamiento central del texto
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Ver todas",
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
    } else { // Si no hay familias
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
        ){
            Column(
                modifier = Modifier
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isLoading) { // Si esta cargando
                    CircularProgressIndicator(
                        modifier = Modifier.size(30.dp),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 2.dp
                    )
                } else { // Si no esta cargando -> no hay resultados | Otro error
                    Icon(
                        imageVector = Icons.Default.SearchOff,
                        contentDescription = "Sin resultados",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(30.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = "No se han encontrado familias", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}