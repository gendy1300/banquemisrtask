package com.ahmedelgendy.banquemisrtask

import org.junit.Assert.assertEquals
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class UnitTestingClass {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }


    @Test
    fun testGetCurrencies() {
        var getCurrencies = "EGP"

        getCurrencies += ",USD"
        getCurrencies += ",EUR"
        getCurrencies += ",JPY"
        getCurrencies += ",GBP"
        getCurrencies += ",CNY"
        getCurrencies += ",AUD"
        getCurrencies += ",CAD"
        getCurrencies += ",CHF"
        getCurrencies += ",HKD"
        getCurrencies += ",SGD"
        getCurrencies += ",SEK"

        assertEquals("EGP,USD,EUR,JPY,GBP,CNY,AUD,CAD,CHF,HKD,SGD,SEK", getCurrencies)

    }


    @Test
    fun testGetDate() {
        var date = ""
        val calendar = Calendar.getInstance()

        val sdf = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH)
        calendar.time = sdf.parse("Mon Dec 4 12:02:37 GMT 2020")

        calendar.add(Calendar.DAY_OF_YEAR, -3)
        val newDate = calendar.time


        date = SimpleDateFormat("yyyy-MM-dd").format(newDate)

        assertEquals("2020-12-01", date)
    }

}