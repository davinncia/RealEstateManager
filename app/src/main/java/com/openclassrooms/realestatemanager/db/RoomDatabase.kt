package com.openclassrooms.realestatemanager.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.dao.PictureDao
import com.openclassrooms.realestatemanager.dao.PoiDao
import com.openclassrooms.realestatemanager.dao.PropertyDao
import com.openclassrooms.realestatemanager.model.Picture
import com.openclassrooms.realestatemanager.model.Poi
import com.openclassrooms.realestatemanager.model.PoiForProperty
import com.openclassrooms.realestatemanager.model.Property
import kotlin.concurrent.thread

@Database(entities = [Property::class, Picture::class, Poi::class, PoiForProperty::class], version = 1, exportSchema = false)
abstract class PropertyRoomDatabase : RoomDatabase() {

    abstract fun propertyDao(): PropertyDao
    abstract fun pictureDao(): PictureDao
    abstract fun poiDao(): PoiDao

    companion object {
        //Singleton
        @Volatile
        private var INSTANCE: PropertyRoomDatabase? = null

        fun getDatabase(context: Context): PropertyRoomDatabase =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
                }

        private fun buildDatabase(context: Context) =
                Room.databaseBuilder(context.applicationContext, PropertyRoomDatabase::class.java, "property_database")
                        //Prepopulate
                        .addCallback(object : Callback() {
                            override fun onCreate(db: SupportSQLiteDatabase) {
                                super.onCreate(db)
                                thread {
                                    getDatabase(context).poiDao().insert(defaultPoiList(context))
                                }
                            }
                        })
                        .build()

        private fun defaultPoiList(context: Context) = listOf(
                Poi(context.resources.getString(R.string.doctor), R.drawable.ic_doctor),
                Poi(context.resources.getString(R.string.school), R.drawable.ic_school),
                Poi(context.resources.getString(R.string.park), R.drawable.ic_park),
                Poi(context.resources.getString(R.string.station), R.drawable.ic_train),
                Poi(context.resources.getString(R.string.grocery), R.drawable.ic_grocery))
    }
}