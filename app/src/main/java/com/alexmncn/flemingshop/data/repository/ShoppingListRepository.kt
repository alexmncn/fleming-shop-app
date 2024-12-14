package com.alexmncn.flemingshop.data.repository

import android.util.Log
import com.alexmncn.flemingshop.data.db.AppDatabase
import com.alexmncn.flemingshop.data.db.ArticleItem
import kotlinx.coroutines.flow.Flow
import java.math.BigInteger

class ShoppingListRepository(db: AppDatabase) {
    private val articleItemDao = db.articleItemDao()

    suspend fun getAllArticles(): Flow<List<ArticleItem>> {
        return articleItemDao.getAll()
    }

    suspend fun deleteAllArticles() {
        articleItemDao.deleteAll()
    }

    fun getArticleByCodebar(codebar: BigInteger): Flow<ArticleItem?> {
        return articleItemDao.getByCodebarFlow(codebar.toLong())
    }

    suspend fun deleteArticleByCodebar(codebar: BigInteger) {
        articleItemDao.deleteByCodebar(codebar.toLong())
    }

    suspend fun insertArticle(articleItem: ArticleItem) {
        val existingArticle = articleItemDao.getByCodebar(articleItem.codebar)

        if (existingArticle == null && articleItem.quantity > 0) {
            // Si no existe y la cantidad es positiva, lo insertamos
            articleItemDao.insert(articleItem)
        } else { // Si ya existe
            // Calculamos la nueva cantidad
            val newQuantity = existingArticle?.quantity?.plus(articleItem.quantity)

            if (newQuantity != null) {
                if (newQuantity > 0) {
                    // Si la nueva cantidad es positiva, lo actualizamos
                    articleItemDao.updateQuantity(articleItem.codebar, newQuantity)
                } else {
                    // Sino, lo eliminamos
                    articleItemDao.deleteByCodebar(articleItem.codebar)
                }
            }
        }
    }
}