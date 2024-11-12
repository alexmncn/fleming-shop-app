package com.alexmncn.flemingshop.ui.main

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
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


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen() {
    FlemingShopTheme {
        Scaffold(
            topBar = { MainTopBar() },
            content = {},
        )
    }
}

@Preview
@Composable
fun PreviewMainScreen() {
    MainScreen()
}