package com.alexmncn.flemingshop.data.repository

import com.alexmncn.flemingshop.data.db.AppDatabase
import com.alexmncn.flemingshop.data.db.ArticleItem

class ShoppingListRepository(db: AppDatabase) {
    val articleItemDao = db.articleItemDao()

    suspend fun getAllArticles(): List<ArticleItem> {
        return articleItemDao.getAll()
    }

    suspend fun getArticleByCodebar(codebar: Int): ArticleItem? {
        return articleItemDao.getByCodebar(codebar)
    }

    suspend fun deleteArticleByCodebar(codebar: Int) {
        articleItemDao.deleteByCodebar(codebar)
    }

    suspend fun insertArticle(articleItem: ArticleItem) {
        articleItemDao.insert(articleItem)
    }
}