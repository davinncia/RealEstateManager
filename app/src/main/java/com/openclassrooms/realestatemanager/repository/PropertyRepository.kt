package com.openclassrooms.realestatemanager.repository

import android.content.Context
import com.openclassrooms.realestatemanager.dao.PropertyDao
import com.openclassrooms.realestatemanager.db.PropertyRoomDatabase
import com.openclassrooms.realestatemanager.model.Property

class PropertyRepository(context: Context) {

    private val propertyDao: PropertyDao

    init {
        val db = PropertyRoomDatabase.getDatabase(context)
        propertyDao = db.propertyDao()
    }

    // Room executes all queries on a separate thread.
    val allProperties = propertyDao.getAllProperties()

    suspend fun insert(property: Property) {
        propertyDao.insertProperty(property)
    }

    suspend fun update(property: Property) {
        propertyDao.updateProperty(property)
    }

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