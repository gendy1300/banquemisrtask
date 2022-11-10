package com.ahmedelgendy.banquemisrtask.general.data.remote

import com.ahmedelgendy.banquemisrtask.convert.data.model.convert.ConvertResponse
import com.ahmedelgendy.banquemisrtask.convert.data.model.currencies.CurrenciesResponse
import com.ahmedelgendy.banquemisrtask.history.data.model.PastRatesResponse
import retrofit2.http.*

interface ConvertApis {


    @GET("convert")
    suspend fun convert(
        @Query("from") from: String,
        @Query("to") to: String,
        @Query("amount") amount: String,
        @Query("date") date: String? = null
    ): ConvertResponse


    @GET("symbols")
    suspend fun getCountries(
    ): CurrenciesResponse


    @GET("timeseries")
    suspend fun getPastRates(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
        @Query("base") fromCurrency: String? = null,
        @Query("symbols") toCurrencies: String? = null,
    ): PastRatesResponse

}
