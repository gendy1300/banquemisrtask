package com.ahmedelgendy.banquemisrtask.general.network


sealed class Resource<out T> {
    data class Success<out T>(val value: T) : Resource<T>()
    data class Failure(
        val isNetworkError: Boolean, val errorCode: Int?,val cause:String
    ) : Resource<Nothing>()

    object Loading : Resource<Nothing>()
}