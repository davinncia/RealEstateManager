package com.openclassrooms.realestatemanager

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.openclassrooms.realestatemanager.dao.PictureDao
import com.openclassrooms.realestatemanager.db.PropertyRoomDatabase
import com.openclassrooms.realestatemanager.model.Picture
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PictureDaoTest {

    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var pictureDao: PictureDao
    private lateinit var db: PropertyRoomDatabase

    //DUMMY
    val p1 = Picture("uri1", 1)
    val p2 = Picture("uri2", 2)
    val p3 = Picture("uri3", 1)

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, PropertyRoomDatabase::class.java).build()
        pictureDao = db.pictureDao()

        //Insert dummy data
        runBlocking {
            pictureDao.insertPicture(p1)
            pictureDao.insertPicture(p2)
            pictureDao.insertPicture(p3)
        }

    }

    @After
    fun closeDb() {
        db.close()
    }

    //Read
    @Test
    fun getPicturesForProperty() {
        //GIVEN
        //WHEN
        val list = pictureDao.getAllPictures(2).getOrAwaitValue()
        //THEN
        Assert.assertEquals(1, list.size)
        Assert.assertEquals("uri2", list[0].strUri)
    }

    @Test
    fun getPictureCountForProperty() {
        //GIVEN
        var count: Int? = null
        //WHEN
        runBlocking {
            count = pictureDao.getPictureCount(1)
        }
        //THEN
        Assert.assertEquals(2, count)
    }

    //Delete
    @Test
    fun removePic() {
        //GIVEN
        var list: List<Picture>? = null
        p1.id = 1 //Room needs a PrimaryKey to perform @Delete
        //WHEN
        runBlocking {
            pictureDao.deletePicture(p1)
            list = pictureDao.getAllPictures(1).getOrAwaitValue()
        }
        //THEN
        Assert.assertEquals(1, list?.size)
    }

}