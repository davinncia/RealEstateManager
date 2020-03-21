package com.openclassrooms.realestatemanager.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.openclassrooms.realestatemanager.model.Poi
import com.openclassrooms.realestatemanager.model.PoiForProperty

@Dao
interface PoiDao {

    @Query("SELECT * FROM poi_table")
    fun getAllPoi(): LiveData<List<Poi>>

    //not suspend because called in a different thread on db creation
    @Insert
    fun insert(poi: List<Poi>)

    @Query("SELECT * FROM poi_for_property WHERE property_id = :propertyId")
    fun getPoiForProperty(propertyId: Int): LiveData<List<PoiForProperty>>

    @Query("SELECT poi_name FROM poi_for_property WHERE property_id = :propertyId")
    suspend fun getPoiNamesForProperty(propertyId: Int): List<String>

    @Insert
    suspend fun insertPoiForProperty(poiForProperty: List<PoiForProperty>)

    @Query("DELETE FROM poi_for_property WHERE property_id = :propertyId")
    suspend fun deleteAllPoiForProperty(propertyId: Int)

}