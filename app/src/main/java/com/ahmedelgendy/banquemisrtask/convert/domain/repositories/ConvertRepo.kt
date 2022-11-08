package com.ahmedelgendy.banquemisrtask.convert.domain.repositories

import com.ahmedelgendy.banquemisrtask.convert.data.model.currencies.CurrenciesResponse
import com.ahmedelgendy.banquemisrtask.general.network.Resource
import com.ahmedelgendy.banquemisrtask.general.network.SafeApiCall

interface ConvertRepo: SafeApiCall {

    suspend fun getCurrencies(): Resource<CurrenciesResponse>


}
