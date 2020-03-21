package com.openclassrooms.realestatemanager.repository

import android.content.Context
import com.openclassrooms.realestatemanager.dao.PictureDao
import com.openclassrooms.realestatemanager.dao.PoiDao
import com.openclassrooms.realestatemanager.dao.PropertyDao
import com.openclassrooms.realestatemanager.db.PropertyRoomDatabase
import com.openclassrooms.realestatemanager.model.Picture
import com.openclassrooms.realestatemanager.model.PoiForProperty
import com.openclassrooms.realestatemanager.model.Property

class PropertyRepository(context: Context) {

    private val propertyDao: PropertyDao
    private val pictureDao : PictureDao
    private val poiDao: PoiDao

    init {
        val db = PropertyRoomDatabase.getDatabase(context)
        propertyDao = db.propertyDao()
        pictureDao = db.pictureDao()
        poiDao = db.poiDao()
    }

    //--------------------------------------------------------------------------------------------//
    //                                    P R O P E R T Y
    //--------------------------------------------------------------------------------------------//
    //CREATE
    suspend fun insert(property: Property) {
        propertyDao.insertProperty(property)
    }
    //READ
    val allProperties = propertyDao.getAllProperties()

    suspend fun getProperty(id: Int): Property = propertyDao.getProperty(id)

    suspend fun advancedSearch(minPrice: Int, maxPrice: Int, minArea: Int, maxArea: Int,
                               city: String, minEpoch: Long, isSold: List<Boolean>): List<Property> =
            propertyDao.advancedSearch(minPrice, maxPrice, minArea, maxArea, city, minEpoch, isSold)

    suspend fun getLastId(): Int = propertyDao.getLastId()
    //UPDATE
    suspend fun update(property: Property) {
        propertyDao.updateProperty(property)
    }

    suspend fun updateThumbnail(uri: String, propertyId: Int) {
        propertyDao.updateThumbnail(uri, propertyId)
    }

    suspend fun updateSaleStatus(propertyId: Int, isSold: Boolean, timeInMillis: Long) {
        propertyDao.changeSaleStatus(propertyId, isSold, timeInMillis)
    }

    //--------------------------------------------------------------------------------------------//
    //                                    P I C T U R E S
    //--------------------------------------------------------------------------------------------//
    fun getPictures(propertyId: Int) = pictureDao.getAllPictures(propertyId)

    suspend fun getPictureCount(propertyId: Int): Int = pictureDao.getPictureCount(propertyId)

    suspend fun insertPicture(picture: Picture) = pictureDao.insertPicture(picture)

    suspend fun deletePicture(picture: Picture) = pictureDao.deletePicture(picture)


    //--------------------------------------------------------------------------------------------//
    //                                          P O I
    //--------------------------------------------------------------------------------------------//
    suspend fun getPoiNames(propertyId: Int) = poiDao.getPoiNamesForProperty(propertyId)

    fun getPoiList(propertyId: Int) = poiDao.getPoiForProperty(propertyId)

    suspend fun addPoiForProperty(poiForPropertyList: List<PoiForProperty>) =
            poiDao.insertPoiForProperty(poiForPropertyList)

    suspend fun deleteAllPoiForProperty(propertyId: Int) =
            poiDao.deleteAllPoiForProperty(propertyId)

    companion object {
        //Singleton
        private var INSTANCE: PropertyRepository? = null

        fun getInstance(context: Context): PropertyRepository{

            if (INSTANCE == null) {
                synchronized(PropertyRepository) {
                    if (INSTANCE == null) {
                        INSTANCE = PropertyRepository(context)
                    }
                }
            }
            return INSTANCE!!
        }
    }

}