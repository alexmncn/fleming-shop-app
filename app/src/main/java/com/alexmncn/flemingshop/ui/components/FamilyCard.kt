package com.alexmncn.flemingshop.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.alexmncn.flemingshop.data.model.Family

@Composable
fun FamilyCard(family: Family, onShowFamily: (codfam: Int, nomfam: String) -> Unit) {

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .padding(top = 2.dp, bottom = 4.dp) // Safe zone for card shadow
            .fillMaxWidth()
            .clickable { onShowFamily(family.codfam, family.nomfam) },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Text(text = family.nomfam, modifier = Modifier.padding(10.dp))
    }
}