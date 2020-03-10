package com.openclassrooms.realestatemanager.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.openclassrooms.realestatemanager.model.Property

@Dao
interface PropertyDao {

    @Query("SELECT * FROM property_table")
    fun getAllProperties(): LiveData<List<Property>> //Automatically asynchronous

    @Query("SELECT * FROM property_table WHERE id = :id")
    suspend fun getProperty(id: Int): Property

    @Query("SELECT id FROM property_table ORDER BY id DESC LIMIT 1")
    suspend fun getLastId(): Int

    @Insert
    suspend fun insertProperty(property: Property)

    @Update
    suspend fun updateProperty(property: Property)

    @Query("UPDATE property_table SET thumbnail_uri = :uri WHERE id = :propertyId")
    suspend fun updateThumbnail(uri: String, propertyId: Int)

    @Query("UPDATE property_table SET is_sold = :isSold, selling_time = :timeInMillis WHERE id = :propertyId")
    suspend fun changeSaleStatus(propertyId: Int, isSold: Boolean, timeInMillis: Long)

}