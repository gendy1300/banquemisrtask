package com.ahmedelgendy.banquemisrtask.history.domain.repositories

import com.ahmedelgendy.banquemisrtask.convert.data.model.currencies.CurrenciesResponse
import com.ahmedelgendy.banquemisrtask.general.network.Resource
import com.ahmedelgendy.banquemisrtask.general.network.SafeApiCall
import com.ahmedelgendy.banquemisrtask.history.data.model.PastRatesResponse

interface HistoryRepo : SafeApiCall {

    suspend fun getPastRates(startDate:String,endDate:String): Resource<PastRatesResponse>


}
