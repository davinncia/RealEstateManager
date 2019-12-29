package com.openclassrooms.realestatemanager

import com.openclassrooms.realestatemanager.utils.Utils
import org.junit.Assert
import org.junit.Test
import java.util.*
import kotlin.collections.ArrayList


class UtilsTest {

    @Test
    fun dollarConvector_ReturnCorrect_Euro_value(){
        //GIVEN
        val dollars = arrayOf(23, 11, 90, 10034)
        val euros = ArrayList<Int>()
        //WHEN
        for (i in dollars){
            euros.add(Utils.convertDollarToEuro(i))
        }
        //THEN
        Assert.assertArrayEquals(arrayOf(21, 10, 81, 9031), euros.toArray())
    }

    @Test
    fun euroConvector_ReturnCorrect_Dollar_value(){
        //GIVEN
        val euros = arrayOf(21, 10, 81, 9031)
        val dollars = ArrayList<Int>()
        //WHEN
        for (i in euros){
            dollars.add(Utils.convertEuroToDollar(i))
        }
        //THEN
        Assert.assertArrayEquals(arrayOf(23, 11, 90, 10024), dollars.toArray())
    }

    @Test
    fun getTodayDateCorrectFormat(){
        //GIVEN
        val date = Date(1576772937965L)
        //WHEN
        val today = Utils.formatTodayDate(date)
        //THEN
        Assert.assertEquals("19/12/2019", today)
    }
}