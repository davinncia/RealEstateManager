package com.openclassrooms.realestatemanager.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.openclassrooms.realestatemanager.dao.PropertyDao
import com.openclassrooms.realestatemanager.model.Property

@Database(entities = arrayOf(Property::class), version = 1, exportSchema = false)
public abstract class PropertyRoomDatabase : RoomDatabase() {

    abstract fun propertyDao(): PropertyDao

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
                        "word_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}