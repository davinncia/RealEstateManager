package com.openclassrooms.realestatemanager.dao

import android.database.Cursor
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.openclassrooms.realestatemanager.model.Property

@Dao
interface PropertyDao {

    //--------------------------------------------------------------------------------------------//
    //                                            C R E A T E
    //--------------------------------------------------------------------------------------------//
    @Insert
    suspend fun insertProperty(property: Property)

    //--------------------------------------------------------------------------------------------//
    //                                              R E A D
    //--------------------------------------------------------------------------------------------//
    @Query("SELECT * FROM property_table")
    fun getAllProperties(): LiveData<List<Property>> //Automatically asynchronous

    @Query("SELECT * FROM property_table WHERE id = :id")
    suspend fun getProperty(id: Int): Property

    @Query("SELECT id FROM property_table ORDER BY id DESC LIMIT 1")
    suspend fun getLastId(): Int

    //ADVANCED SEARCH
    //Rmq: empty string fields are replaced by "%"
    @Query("""SELECT * FROM property_table 
        WHERE price BETWEEN :minPrice AND :maxPrice
        AND area BETWEEN :minArea AND :maxArea
        AND city LIKE :city 
        AND creation_time >= :minCreationEpoch 
        AND is_sold IN (:isSold)""")
    suspend fun advancedSearch(
            minPrice: Int, maxPrice: Int,
            minArea: Int, maxArea: Int,
            city: String, minCreationEpoch: Long,
            isSold: List<Boolean>)
            : List<Property>
    //For ContentProvider
    @Query("SELECT * FROM property_table WHERE id = :propertyId")
    fun getPropertyWithCursor(propertyId: Long): Cursor


    //--------------------------------------------------------------------------------------------//
    //                                            U P D A T E
    //--------------------------------------------------------------------------------------------//
    @Update
    suspend fun updateProperty(property: Property)

    @Query("UPDATE property_table SET thumbnail_uri = :uri WHERE id = :propertyId")
    suspend fun updateThumbnail(uri: String, propertyId: Int)

    @Query("UPDATE property_table SET is_sold = :isSold, selling_time = :timeInMillis WHERE id = :propertyId")
    suspend fun changeSaleStatus(propertyId: Int, isSold: Boolean, timeInMillis: Long)

}