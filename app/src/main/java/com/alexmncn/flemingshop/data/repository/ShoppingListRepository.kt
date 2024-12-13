package com.alexmncn.flemingshop.data.repository

import com.alexmncn.flemingshop.data.db.AppDatabase
import com.alexmncn.flemingshop.data.db.ArticleItem

class ShoppingListRepository(db: AppDatabase) {
    val articleItemDao = db.articleItemDao()

    suspend fun getAllArticles(): List<ArticleItem> {
        return articleItemDao.getAll()
    }
}