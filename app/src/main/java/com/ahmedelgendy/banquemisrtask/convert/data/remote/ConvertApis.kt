package com.ahmedelgendy.banquemisrtask.convert.data.remote

import com.ahmedelgendy.banquemisrtask.convert.data.model.convert.ConvertResponse
import com.ahmedelgendy.banquemisrtask.convert.data.model.currencies.CurrenciesResponse
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

}
