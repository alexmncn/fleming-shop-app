package com.alexmncn.flemingshop.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController


@Composable
fun HomeScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .padding(10.dp)
    ) {
        // Featured Articles
        Button(
            onClick = {
                navController.navigate("featured_articles")
            },
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(text = "Featured Articles")
        }
        Spacer(modifier = Modifier.height(10.dp))

        // New Articles
        Button(
            onClick = {
                navController.navigate("new_articles")
            },
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(text = "New Articles")
        }
        Spacer(modifier = Modifier.height(10.dp))

        // Families
        Button(
            onClick = {
                navController.navigate("families")
            },
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(text = "Families")
        }
        Spacer(modifier = Modifier.height(10.dp))

        // Search Articles
        Button(
            onClick = {
                navController.navigate("search_articles")
            },
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(text = "Search Articles")
        }
        Spacer(modifier = Modifier.height(10.dp))


        // Codebar Reader
        Button(
            onClick = {
                navController.navigate("barcode_scanner")
            },
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(text = "Codebar Reader")
        }
    }
}