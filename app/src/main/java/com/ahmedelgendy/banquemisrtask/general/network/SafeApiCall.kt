package com.ahmedelgendy.banquemisrtask.general.network

import com.ahmedelgendy.banquemisrtask.general.network.intercepter.NoConnectivityException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

interface SafeApiCall {

    suspend fun <T> safeApiCall(
        apiCall: suspend () -> T
    ): Resource<T> {
        return withContext(Dispatchers.IO) {
            try {
                Resource.Success(apiCall.invoke())
            } catch (throwable: Throwable) {
                when (throwable) {
                    is HttpException -> {
                        Resource.Failure(
                            false,
                            throwable.code(),
                            throwable.message().toString()
                        )
                    }

                    is NoConnectivityException -> {
                        Resource.Failure(
                            true,
                            0,
                            "no Internet Connection"
                        )
                    }


                    is java.net.SocketTimeoutException -> {

                        Resource.Failure(
                            true,
                            0,
                            "Time out"
                        )
                    }

                    else -> {
                        Resource.Failure(
                            false, null, throwable.message.toString()
                        )
                    }
                }
            }
        }
    }
}