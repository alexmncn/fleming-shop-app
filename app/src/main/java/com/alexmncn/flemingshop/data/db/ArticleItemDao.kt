package com.alexmncn.flemingshop.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import java.math.BigInteger

@Dao
interface ArticleItemDao  {
    @Query("SELECT * FROM shoppingList")
    fun getAll(): Flow<List<ArticleItem>>

    @Query("DELETE FROM shoppingList")
    suspend fun deleteAll()

    @Query("SELECT * FROM shoppingList WHERE codebar = :codebar")
    fun getByCodebarFlow(codebar: Long): Flow<ArticleItem?>

    @Query("SELECT * FROM shoppingList WHERE codebar = :codebar")
    suspend fun getByCodebar(codebar: Long): ArticleItem?

    @Query("DELETE FROM shoppingList WHERE codebar = :codebar")
    suspend fun deleteByCodebar(codebar: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(articleItem: ArticleItem)

    @Query("UPDATE shoppingList SET quantity = :quantity WHERE codebar = :codebar")
    suspend fun updateQuantity(codebar: Long, quantity: Int)
}