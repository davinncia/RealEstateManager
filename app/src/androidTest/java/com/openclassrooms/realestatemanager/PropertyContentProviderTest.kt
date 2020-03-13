package com.openclassrooms.realestatemanager

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentUris.withAppendedId
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.openclassrooms.realestatemanager.db.PropertyRoomDatabase
import com.openclassrooms.realestatemanager.provider.PropertyContentProvider
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PropertyContentProviderTest {

    //TODO: Content provider test

    // FOR DATA
    private lateinit var mContentResolver: ContentResolver

    // DATA SET FOR TEST
    private val propertyID: Long = 1

    @Before
    fun setUp() {
        Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getInstrumentation().context,
                PropertyRoomDatabase::class.java)
                .allowMainThreadQueries()
                .build()
        mContentResolver = InstrumentationRegistry.getInstrumentation().context.contentResolver
    }

    @Test
    fun getPropertyWhenNoPropertyInserted() {
        //WHEN
        val cursor = mContentResolver.query(withAppendedId(PropertyContentProvider.URI_PROPERTY, propertyID), null, null, null, null)
        //THEN
        Assert.assertNotNull(cursor)
        Assert.assertTrue(cursor!!.count == 1) //TODO: Why not 0 ??
        cursor.close()
    }

    @Test
    fun getProperty() {
        // GIVEN
        //insert in db

        //WHEN
        val cursor = mContentResolver.query(ContentUris.withAppendedId(PropertyContentProvider.URI_PROPERTY, propertyID), null, null, null, null)
        //THEN
        Assert.assertNotNull(cursor)
        Assert.assertTrue(cursor!!.count == 1)
        Assert.assertTrue(cursor.moveToFirst())
        Assert.assertEquals(cursor.getString(cursor.getColumnIndexOrThrow("descriptionProperty")), "Cool")
    }



}