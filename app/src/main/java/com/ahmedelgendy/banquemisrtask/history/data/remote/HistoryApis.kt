package com.ahmedelgendy.banquemisrtask.history.data.remote

import com.ahmedelgendy.banquemisrtask.history.data.model.PastRatesResponse
import retrofit2.http.*

interface HistoryApis {

    @GET("timeseries")
    suspend fun getPastRates(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
    ): PastRatesResponse


}
