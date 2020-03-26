package com.openclassrooms.realestatemanager

import android.content.ContentResolver
import android.content.ContentUris
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.openclassrooms.realestatemanager.model.Address
import com.openclassrooms.realestatemanager.model.Property
import com.openclassrooms.realestatemanager.provider.PropertyContentProvider
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PropertyContentProviderTest {

    private lateinit var contentResolver: ContentResolver

    // DATA SET FOR TEST
    private val propertyID = 1
    private val nonExistingPropertyId = -1
    private val property = Property("HOUSE", 100000, 30F, 2, "Cool flat.",
            Address("NY", "street", 10), 0, "Phil")


    @Before
    fun setUp() {
        //CONTENT RESOLVER
        contentResolver = InstrumentationRegistry.getInstrumentation().context.contentResolver
    }

    //TODO ProviderTest: Run on test db ?
/*
    @Test
    fun getPropertyWhenNoPropertyInserted() { //It's actually going on real db, so not empty !
        //WHEN
        val cursor = contentResolver.query(withAppendedId(PropertyContentProvider.URI_PROPERTY, propertyID.toLong()), null, null, null, null)
        //THEN
        Assert.assertNotNull(cursor)
        Assert.assertTrue(cursor!!.count == 0)
        cursor.close()
    }
 */

    @Test
    fun whenFetchingNonExistingPropertyThenCursorIsEmpty() {
        //WHEN
        val cursor = contentResolver.query(ContentUris.withAppendedId(PropertyContentProvider.URI_PROPERTY, nonExistingPropertyId.toLong()), null, null, null, null)
        //THEN
        Assert.assertNotNull(cursor)
        Assert.assertTrue(cursor!!.count == 0)
    }

    @Test
    fun getFirstPropertyOfTablebIfExists() {
        //WHEN
        val cursor = contentResolver.query(ContentUris.withAppendedId(PropertyContentProvider.URI_PROPERTY, propertyID.toLong()), null, null, null, null)
        //THEN
        if (cursor!!.count == 1) {
            //Make sure we're not running on an empty db
            Assert.assertTrue(cursor.moveToFirst())
            Assert.assertEquals(propertyID, cursor.getInt(cursor.getColumnIndexOrThrow("id")))
        }
    }



}