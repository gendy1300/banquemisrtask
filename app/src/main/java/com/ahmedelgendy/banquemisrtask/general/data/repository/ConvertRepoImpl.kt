package com.ahmedelgendy.banquemisrtask.general.data.repository

import com.ahmedelgendy.banquemisrtask.general.data.remote.ConvertApis
import com.ahmedelgendy.banquemisrtask.general.data.domain.repositories.ConvertRepo
import javax.inject.Inject

class ConvertRepoImpl @Inject constructor(
    private var api: ConvertApis
) : ConvertRepo {


    override suspend fun getCurrencies() =
        safeApiCall {
            api.getCountries()
        }


    override suspend fun convert(
        from: String,
        to: String,
        amount: String
    ) =
        safeApiCall {
            api.convert(from, to, amount)
        }


    override suspend fun getPastRates(
        startDate: String,
        endDate: String,
        fromCurrency: String?,
        toCurrencies: String?
    ) = safeApiCall {
        api.getPastRates(startDate, endDate, fromCurrency, toCurrencies)
    }

}
