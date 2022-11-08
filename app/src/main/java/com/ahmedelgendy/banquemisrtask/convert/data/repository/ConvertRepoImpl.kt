package com.ahmedelgendy.banquemisrtask.convert.data.repository

import com.ahmedelgendy.banquemisrtask.convert.data.remote.ConvertApis
import com.ahmedelgendy.banquemisrtask.convert.domain.repositories.ConvertRepo
import javax.inject.Inject

class ConvertRepoImpl @Inject constructor(
    private var api: ConvertApis
) : ConvertRepo {


    override suspend fun getCurrencies() =
        safeApiCall {
            api.getCountries()
        }

}
