package com.openclassrooms.realestatemanager

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.openclassrooms.realestatemanager.dao.PropertyDao
import com.openclassrooms.realestatemanager.db.PropertyRoomDatabase
import com.openclassrooms.realestatemanager.model.Address
import com.openclassrooms.realestatemanager.model.Property
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class PropertyDaoTest {

    private lateinit var propertyDao: PropertyDao
    private lateinit var db: PropertyRoomDatabase


    //DUMMY
    private val p1 = Property("HOUSE", 100_000, 100F, 1, "One",
            Address("city1", "", 1, 1.0, 1.0), 1L, "")
    private val p2 = Property("HOUSE", 200_000, 200F, 2, "One",
            Address("city2", "", 1, 2.0, 2.0), 2L, "")

    @Before
    fun createDummyDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, PropertyRoomDatabase::class.java).build()
        propertyDao = db.propertyDao()

        //Insert dummy data

        runBlocking {
            propertyDao.insertProperty(p1)
            propertyDao.insertProperty(p2)
        }
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun readProperty() {
        //GIVEN
        var dbProperty: Property? = null
        //WHEN
        runBlocking {
            dbProperty = propertyDao.getProperty(1)
        }
        //THEN
        Assert.assertEquals(p1, dbProperty)
    }

    @Test
    fun getLastPropertyId() {
        //GIVEN
        var lastId: Int? = null
        //WHEN
        runBlocking {
            lastId = propertyDao.getLastId() }
        //THEN
        Assert.assertEquals(2, lastId)
    }

    //ADVANCED SEARCH
    @Test
    fun getPropertyGivenMinPrice() {
        //GIVEN
        val minPrice = 150_000
        var result: List<Property>? = null
        //WHEN
        runBlocking {
             result = propertyDao.advancedSearch(minPrice, 1_000_000, 0, 10_000, "%",
            0L, listOf(true, false))
        }
        //THEN
        Assert.assertTrue(result?.size == 1)
        Assert.assertTrue(result?.get(0)?.id == 2) //P2
    }

    @Test
    fun getPropertyGivenCity() {
        //GIVEN
        val city = "city2"
        var result: List<Property>? = null
        //WHEN
        runBlocking {
            result = propertyDao.advancedSearch(0, 1_000_000, 0, 10_000,
                    city, 0L, listOf(true, false))
        }
        //THEN
        Assert.assertTrue(result?.size == 1)
        Assert.assertTrue(result?.get(0) == p2) //P2
    }

    @Test
    fun getPropertyGivenSaleStatus() {
        //GIVEN
        val soldStatus = listOf(true, true)
        var result: List<Property>? = null
        //WHEN
        runBlocking {
            result = propertyDao.advancedSearch(0, 1_000_000, 0, 10_000, "%",
                    0L, soldStatus)
        }
        //THEN
        Assert.assertTrue(result?.size == 0)
    }

}