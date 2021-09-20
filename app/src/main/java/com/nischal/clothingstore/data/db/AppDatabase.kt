package com.nischal.clothingstore.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.nischal.clothingstore.data.db.dao.ShoppingListDao
import com.nischal.clothingstore.ui.models.ProductVariant

@Database(
    entities = [ProductVariant::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class  AppDatabase: RoomDatabase(){
    abstract fun getShoppingListDao(): ShoppingListDao
}