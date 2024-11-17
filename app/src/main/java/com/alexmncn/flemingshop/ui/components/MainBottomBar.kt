package com.alexmncn.flemingshop.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun MainBottomBar() {
    BottomAppBar (
        modifier = Modifier.height(50.dp),
        containerColor = Color.White,
        contentColor = MaterialTheme.colorScheme.primary
    ) {

    }
}