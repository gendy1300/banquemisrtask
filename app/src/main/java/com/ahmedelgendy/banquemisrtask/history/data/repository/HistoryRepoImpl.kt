package com.ahmedelgendy.banquemisrtask.history.data.repository

import com.ahmedelgendy.banquemisrtask.history.data.remote.HistoryApis
import com.ahmedelgendy.banquemisrtask.history.domain.repositories.HistoryRepo
import javax.inject.Inject

class HistoryRepoImpl @Inject constructor(
    private var api: HistoryApis
) : HistoryRepo {


    override suspend fun getPastRates(
        startDate: String,
        endDate: String
    ) = safeApiCall {
            api.getPastRates(startDate, endDate)
        }


}
