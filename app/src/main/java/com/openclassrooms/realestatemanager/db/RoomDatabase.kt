package com.openclassrooms.realestatemanager.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.openclassrooms.realestatemanager.dao.PictureDao
import com.openclassrooms.realestatemanager.dao.PropertyDao
import com.openclassrooms.realestatemanager.model.Picture
import com.openclassrooms.realestatemanager.model.Property

@Database(entities = [Property::class, Picture::class], version = 1, exportSchema = false)
abstract class PropertyRoomDatabase : RoomDatabase() {

    abstract fun propertyDao(): PropertyDao
    abstract fun pictureDao(): PictureDao

    companion object {
        //Singleton
        @Volatile
        private var INSTANCE: PropertyRoomDatabase? = null

        fun getDatabase(context: Context): PropertyRoomDatabase {

            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                        context.applicationContext,
                        PropertyRoomDatabase::class.java,
                        "property_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}