package com.openclassrooms.realestatemanager.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.openclassrooms.realestatemanager.dao.PoiDao
import com.openclassrooms.realestatemanager.db.PropertyRoomDatabase
import com.openclassrooms.realestatemanager.model.Poi

class PoiRepository(context: Context) {

    private val poiDao: PoiDao

    init {
        val db = PropertyRoomDatabase.getDatabase(context)
        poiDao = db.poiDao()
    }

    fun allPoi(): LiveData<List<Poi>> = poiDao.getAllPoi()
    
    companion object {
        private var INSTANCE: PoiRepository? = null

        fun getInstance(context: Context): PoiRepository {
            INSTANCE ?: synchronized(this) {
                if (INSTANCE == null) {
                    INSTANCE = PoiRepository(context)
                }
            }
            return INSTANCE!!
        }
    }
}