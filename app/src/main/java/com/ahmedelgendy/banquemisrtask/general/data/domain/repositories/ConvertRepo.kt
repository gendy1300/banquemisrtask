package com.ahmedelgendy.banquemisrtask.general.data.domain.repositories

import com.ahmedelgendy.banquemisrtask.convert.data.model.convert.ConvertResponse
import com.ahmedelgendy.banquemisrtask.convert.data.model.currencies.CurrenciesResponse
import com.ahmedelgendy.banquemisrtask.general.network.Resource
import com.ahmedelgendy.banquemisrtask.general.network.SafeApiCall
import com.ahmedelgendy.banquemisrtask.history.data.model.PastRatesResponse

interface ConvertRepo : SafeApiCall {

    suspend fun getCurrencies(): Resource<CurrenciesResponse>

    suspend fun convert(from: String, to: String, amount: String): Resource<ConvertResponse>

    suspend fun getPastRates(
        startDate: String,
        endDate: String,
        fromCurrency: String?,
        toCurrencies: String?
    ): Resource<PastRatesResponse>
}
