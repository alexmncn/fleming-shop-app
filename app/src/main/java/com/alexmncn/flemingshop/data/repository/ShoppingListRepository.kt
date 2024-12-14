package com.alexmncn.flemingshop.data.repository

import com.alexmncn.flemingshop.data.db.AppDatabase
import com.alexmncn.flemingshop.data.db.ArticleItem
import java.math.BigInteger

class ShoppingListRepository(db: AppDatabase) {
    private val articleItemDao = db.articleItemDao()

    suspend fun getAllArticles(): List<ArticleItem> {
        return articleItemDao.getAll()
    }

    suspend fun getArticleByCodebar(codebar: BigInteger): ArticleItem? {
        return articleItemDao.getByCodebar(codebar.toLong())
    }

    suspend fun deleteArticleByCodebar(codebar: BigInteger) {
        articleItemDao.deleteByCodebar(codebar.toLong())
    }

    suspend fun insertArticle(articleItem: ArticleItem) {
        val existingArticle = articleItemDao.getByCodebar(articleItem.codebar)

        if (existingArticle == null) {
            // Si no existe, lo insertamos
            articleItemDao.insert(articleItem)
        } else { // Si ya existe
            // Calculamos la nueva cantidad
            val newQuantity = existingArticle.quantity + articleItem.quantity

            if (newQuantity > 0) {
                // Si es mayor que 0, lo actualizamos
                articleItemDao.updateQuantity(articleItem.codebar, newQuantity)
            } else {
                // Sino, lo eliminamos
                articleItemDao.deleteByCodebar(articleItem.codebar)

            }
        }
    }
}