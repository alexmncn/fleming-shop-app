package com.alexmncn.flemingshop.ui.main

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import com.alexmncn.flemingshop.ui.components.MainTopBar
import com.alexmncn.flemingshop.ui.theme.FlemingShopTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide() // Hide default topbar with app name

        setContent {
            MainScreen()
        }

    }
}


@Composable
fun MainScreen() {
    val context = LocalContext.current
    FlemingShopTheme {
        Scaffold(
            topBar = { MainTopBar() },
            content = { paddingValues ->
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .padding(horizontal = 10.dp)
                ) {
                    // Featured Articles
                    Button(onClick = {
                        val intent = Intent(context, FeaturedArticlesActivity::class.java)
                        context.startActivity(intent)
                    }) {
                        Text(text = "Featured Articles")
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    // New Articles
                    Button(onClick = {
                        val intent = Intent(context, NewArticlesActivity::class.java)
                        context.startActivity(intent)
                    }) {
                        Text(text = "New Articles")
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    // Codebar Reader
                    Button(onClick = {
                        val intent = Intent(context, CodebarScannerActivity::class.java)
                        context.startActivity(intent)
                    }) {
                        Text(text = "Codebar Reader")
                    }
                }
            }
        )
    }
}

@Preview
@Composable
fun PreviewMainScreen() {
    MainScreen()
}