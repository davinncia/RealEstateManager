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

    @Insert
    suspend fun insertProperty(property: Property)

    @Update
    suspend fun updateProperty(property: Property)
}