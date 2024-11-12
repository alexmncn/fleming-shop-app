package com.alexmncn.flemingshop.ui.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.alexmncn.flemingshop.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopBar() {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxHeight()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.fleming_logo),
                    contentDescription = "Tienda Fleming",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .height(24.dp)
                        .fillMaxHeight()
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White,
            titleContentColor = Color.Black
        ),
        modifier = Modifier.height(50.dp)
    )
}