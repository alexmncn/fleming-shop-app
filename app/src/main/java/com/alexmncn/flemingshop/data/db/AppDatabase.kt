package com.alexmncn.flemingshop.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ArticleItem::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun articleItemDao(): ArticleItemDao
}
