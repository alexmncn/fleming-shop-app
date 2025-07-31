package com.alexmncn.flemingshop.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigInteger

@Entity(tableName = "shoppingList")
data class ArticleItem(
    @PrimaryKey(autoGenerate = false) val codebar: String,
    val detalle: String,
    val pvp: Float,
    val quantity: Int
)