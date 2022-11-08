package com.ahmedelgendy.banquemisrtask.convert.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ahmedelgendy.banquemisrtask.convert.data.model.convert.ConvertResponse
import com.ahmedelgendy.banquemisrtask.convert.data.model.currencies.CurrenciesResponse
import com.ahmedelgendy.banquemisrtask.convert.domain.repositories.ConvertRepo
import com.ahmedelgendy.banquemisrtask.general.NetworkCallEvent
import com.ahmedelgendy.banquemisrtask.general.network.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConvertViewModel @Inject constructor(
    private val repo: ConvertRepo
) : ViewModel() {


    private var _currenciesResponse: MutableLiveData<NetworkCallEvent<Resource<CurrenciesResponse>>> =
        MutableLiveData()
    val currenciesResponse: LiveData<NetworkCallEvent<Resource<CurrenciesResponse>>>
        get() = _currenciesResponse


    private var _convertResponse: MutableLiveData<NetworkCallEvent<Resource<ConvertResponse>>> =
        MutableLiveData()
    val convertResponse: LiveData<NetworkCallEvent<Resource<ConvertResponse>>>
        get() = _convertResponse


    fun getCurrencies() = viewModelScope.launch {
        _currenciesResponse.postValue(NetworkCallEvent(Resource.Loading))
        _currenciesResponse.postValue(NetworkCallEvent(repo.getCurrencies()))
    }


    fun convert(from:String,to:String,amount:String) = viewModelScope.launch {
        _convertResponse.postValue(NetworkCallEvent(Resource.Loading))
        _convertResponse.postValue(NetworkCallEvent(repo.convert(from,to,amount)))
    }


    fun clearResponses() {
        _currenciesResponse = MutableLiveData()
        _convertResponse = MutableLiveData()
    }
}
