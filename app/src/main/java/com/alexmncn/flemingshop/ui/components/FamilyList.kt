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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.alexmncn.flemingshop.data.model.Family

@Composable
fun FamilyList(families: List<Family>, isLoading: Boolean, onShowFamily: (codfam: Int, nomfam: String) -> Unit) {
    val scrollState = rememberScrollState()  // Estado de desplazamiento general
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp  // Altura de la pantalla
    val gridColumns = 2 // Numero de columnas en el grid

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
                Text(text = "Familias", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onPrimary)
                Text(text = "${families.size} familias", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimary)

            }
        }

        if (families.isNotEmpty()) { // Si hay familias
            // Grid Familias
            Column(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.heightIn(max = screenHeight) // Limita el alto del grid para que el scroll no cause errores
                ) {
                    // Margen superior vertical contenido
                    // Añade el numero de 'gridColumns' en espaciadores, siendo los elementos de la primera fila espacios,
                    // pero puediendo desplazarlos, y no dejando huecos blancos al scrollear
                    items(gridColumns) {
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    // Familias
                    items(families.size) { index ->
                        FamilyCard(
                            family = families[index],
                            onShowFamily = { codfam, nomfam -> onShowFamily(codfam, nomfam) }
                        )
                    }

                    // Margen inferior vertical contenido
                    items(gridColumns) {
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }
        } else { // Si no hay familias
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
                        Text(text = "No se han encontrado familias", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
    }
}