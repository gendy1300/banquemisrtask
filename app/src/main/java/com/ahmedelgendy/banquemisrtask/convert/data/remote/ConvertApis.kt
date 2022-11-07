package com.ahmedelgendy.banquemisrtask.convert.data.remote

import com.ahmedelgendy.banquemisrtask.convert.data.model.ConvertResponse
import retrofit2.http.*

interface ConvertApis {


    @GET("convert")
    suspend fun convert(
        @Query("from") from: String,
        @Query("to") to: String,
        @Query("amount") amount: String
    ): ConvertResponse

}
