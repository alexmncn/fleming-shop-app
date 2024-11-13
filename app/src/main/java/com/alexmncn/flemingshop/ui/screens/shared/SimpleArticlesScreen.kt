package com.alexmncn.flemingshop.ui.screens.shared

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alexmncn.flemingshop.data.model.Article
import com.alexmncn.flemingshop.ui.components.ArticleList
import com.alexmncn.flemingshop.ui.components.MainTopBar
import com.alexmncn.flemingshop.ui.theme.FlemingShopTheme


@Composable
fun SimpleArticlesScreen(total: Int, articles: List<Article>, listName: String, onShowMore: () -> Unit) {
    FlemingShopTheme {
        Scaffold(
            topBar = { MainTopBar() },
            content = { paddingValues ->
                Column(
                    modifier = Modifier
                        .padding(paddingValues) // Safe zone from topbar
                        .padding(horizontal = 10.dp) // Horizontal margin for the content only
                ) {
                    ArticleList(
                        total = total,
                        articles = articles,
                        listName = listName,
                        onShowMore
                    )
                }
            },
        )
    }
}