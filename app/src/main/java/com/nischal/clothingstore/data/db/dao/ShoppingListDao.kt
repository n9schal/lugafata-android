package com.nischal.clothingstore.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.nischal.clothingstore.ui.models.ProductVariant

@Dao
interface ShoppingListDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(productVariant: ProductVariant): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProductVariants(productVariants: ArrayList<ProductVariant>)

    @Delete
    suspend fun deleteShoppingListItem(productVariant: ProductVariant)

    @Query("SELECT * FROM productVariants")
    fun getShoppingList(): LiveData<List<ProductVariant>>

    @Query("DELETE FROM productVariants")
    suspend fun deleteAllShoppingList()

}