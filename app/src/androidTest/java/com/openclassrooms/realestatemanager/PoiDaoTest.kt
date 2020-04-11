package com.openclassrooms.realestatemanager

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.openclassrooms.realestatemanager.dao.PoiDao
import com.openclassrooms.realestatemanager.db.PropertyRoomDatabase
import com.openclassrooms.realestatemanager.model.PoiForProperty
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PoiDaoTest {

    //For LiveData
    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var poiDao: PoiDao
    private lateinit var db: PropertyRoomDatabase

    private val propertyId = 1
    private val poiName = "poi"

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        db = Room.inMemoryDatabaseBuilder(context, PropertyRoomDatabase::class.java).build()
        poiDao = db.poiDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAndReadPoiForProperty() {
        //WHEN
        runBlocking {
            poiDao.insertPoiForProperty(listOf(PoiForProperty(propertyId, poiName)))
        }
        val poiList = poiDao.getPoiForProperty(propertyId).getOrAwaitValue()
        //THEN
        Assert.assertEquals(poiName, poiList[0].poiName)
    }

    @Test
    fun deleteAllPoiForProperty() {
        //GIVEN
        var poiList: List<String>? = null
        //WHEN
        runBlocking {
            poiDao.insertPoiForProperty(listOf(PoiForProperty(propertyId, poiName)))
            poiDao.deleteAllPoiForProperty(propertyId)
            poiList = poiDao.getPoiNamesForProperty(propertyId)
        }
        //THEN
        Assert.assertTrue(poiList.isNullOrEmpty())
    }

}