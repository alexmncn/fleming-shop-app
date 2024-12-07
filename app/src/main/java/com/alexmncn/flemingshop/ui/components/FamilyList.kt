package com.alexmncn.flemingshop.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.alexmncn.flemingshop.data.model.Family

@Composable
fun FamilyList(families: List<Family>, onShowFamily: (codfam: Int, nomfam: String) -> Unit) {
    val scrollState = rememberScrollState()  // Estado de desplazamiento general
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp  // Altura de la pantalla

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
            Column(
                modifier = Modifier
                    .padding(10.dp)
            ) {
                // Encabezado
                Text(text = "Familias", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimary)
            }
        }

        Column(
            modifier = Modifier
                .padding(10.dp)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.heightIn(max = screenHeight) // Limita el alto del grid para que el scroll no cause errores
            ) {
                items(families.size) { index ->
                    FamilyCard(
                        family = families[index],
                        onShowFamily = { codfam, nomfam -> onShowFamily(codfam, nomfam) }
                    )
                }
            }
        }
    }
}