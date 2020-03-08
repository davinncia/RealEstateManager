package com.openclassrooms.realestatemanager.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.openclassrooms.realestatemanager.model.Picture

@Dao
interface PictureDao {

    @Query("SELECT * FROM picture_table WHERE property_id = :property_id")
    fun getAllPictures(property_id: Int): LiveData<List<Picture>>

    @Insert
    suspend fun insertPicture(picture: Picture)

    @Delete
    suspend fun deletePicture(picture: Picture)
}