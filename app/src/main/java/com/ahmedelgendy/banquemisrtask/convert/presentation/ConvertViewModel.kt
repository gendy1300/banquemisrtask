package com.ahmedelgendy.banquemisrtask.convert.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ahmedelgendy.banquemisrtask.convert.data.model.convert.ConvertResponse
import com.ahmedelgendy.banquemisrtask.convert.data.model.currencies.CurrenciesResponse
import com.ahmedelgendy.banquemisrtask.general.NetworkCallEvent
import com.ahmedelgendy.banquemisrtask.general.data.domain.repositories.ConvertRepo
import com.ahmedelgendy.banquemisrtask.general.network.Resource
import com.ahmedelgendy.banquemisrtask.history.data.model.PastRatesResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ConvertViewModel @Inject constructor(
    private val repo: ConvertRepo
) : ViewModel() {


    private var _currenciesResponse: MutableLiveData<NetworkCallEvent<Resource<CurrenciesResponse>>> =
        MutableLiveData()
    val currenciesResponse: LiveData<NetworkCallEvent<Resource<CurrenciesResponse>>>
        get() = _currenciesResponse


    private var _convertResponse: MutableLiveData<Resource<ConvertResponse>> =
        MutableLiveData()
    val convertResponse: LiveData<Resource<ConvertResponse>>
        get() = _convertResponse

    private var _pastRatesResponse: MutableLiveData<NetworkCallEvent<Resource<PastRatesResponse>>> =
        MutableLiveData()
    val pastRatesResponse: LiveData<NetworkCallEvent<Resource<PastRatesResponse>>>
        get() = _pastRatesResponse


    var fromCurrency: String? = null
    var fromAmount: MutableLiveData<String> = MutableLiveData("1.00")
    var fromCurrencyPosition: Int = 0

    var toCurrency: String? = null
    var toAmount: MutableLiveData<String> = MutableLiveData("1.0")
    var toCurrencyPosition: Int = 0

    var isConvertFragmentLoaded = false

    lateinit var currencies: SortedMap<String, String>


    fun getCurrencies() = viewModelScope.launch {
        _currenciesResponse.postValue(NetworkCallEvent(Resource.Loading))
        _currenciesResponse.postValue(NetworkCallEvent(repo.getCurrencies()))
    }


    fun convert(from: String, to: String, amount: String) = viewModelScope.launch {
        _convertResponse.postValue(Resource.Loading)
        _convertResponse.postValue(repo.convert(from, to, amount))
    }


    fun getPastRates(
        startDate: String, endDate: String,
        fromCurrency: String? = null,
        toCurrencies: String? = null
    ) = viewModelScope.launch {
        _pastRatesResponse.postValue(NetworkCallEvent(Resource.Loading))
        _pastRatesResponse.postValue(
            NetworkCallEvent(
                repo.getPastRates(
                    startDate,
                    endDate,
                    fromCurrency,
                    toCurrencies
                )
            )
        )
    }

    fun clearResponses() {
//        _currenciesResponse = MutableLiveData()
        _convertResponse = MutableLiveData()
    }


}
