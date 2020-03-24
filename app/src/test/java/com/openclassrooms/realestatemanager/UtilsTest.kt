package com.openclassrooms.realestatemanager

import com.openclassrooms.realestatemanager.utils.Utils
import org.junit.Assert
import org.junit.Test
import java.math.BigDecimal
import java.util.*


class UtilsTest {

    @Test
    fun dollarConvector_ReturnCorrect_Euro_value(){
        //GIVEN
        val d1 = BigDecimal.valueOf(23.32)
        val d2 = BigDecimal.valueOf(10034.66)
        //WHEN
        val e1 = Utils.convertDollarToEuro(d1)
        val e2 = Utils.convertDollarToEuro(d2)
        //THEN
        Assert.assertEquals(BigDecimal.valueOf(20.988), e1)
        Assert.assertEquals(BigDecimal.valueOf(9031.194), e2)
    }

    @Test
    fun euroConvector_ReturnCorrect_Dollar_value(){
        //GIVEN
        val euros = arrayOf(21, 10, 81, 9031)
        val e1 = BigDecimal.valueOf(98638.98)
        val e2 = BigDecimal.valueOf(8790.86)
        //WHEN
        val d1 = Utils.convertEuroToDollar(e1)
        val d2 = Utils.convertEuroToDollar(e2)
        //THEN
        Assert.assertEquals(BigDecimal.valueOf(108502.878), d1)
        Assert.assertEquals(BigDecimal.valueOf(9669.946), d2)
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