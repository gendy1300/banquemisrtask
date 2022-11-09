package com.ahmedelgendy.banquemisrtask.history.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ahmedelgendy.banquemisrtask.convert.data.model.convert.ConvertResponse
import com.ahmedelgendy.banquemisrtask.convert.data.model.currencies.CurrenciesResponse
import com.ahmedelgendy.banquemisrtask.general.NetworkCallEvent
import com.ahmedelgendy.banquemisrtask.general.network.Resource
import com.ahmedelgendy.banquemisrtask.history.data.model.PastRatesResponse
import com.ahmedelgendy.banquemisrtask.history.domain.repositories.HistoryRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repo: HistoryRepo
) : ViewModel() {


    private var _pastRatesResponse: MutableLiveData<NetworkCallEvent<Resource<PastRatesResponse>>> =
        MutableLiveData()
    val pastRatesResponse: LiveData<NetworkCallEvent<Resource<PastRatesResponse>>>
        get() = _pastRatesResponse




    fun getPastRates(startDate:String,endDate:String) = viewModelScope.launch {
        _pastRatesResponse.postValue(NetworkCallEvent(Resource.Loading))
        _pastRatesResponse.postValue(NetworkCallEvent(repo.getPastRates(startDate,endDate)))
    }




    fun clearResponses() {
        _pastRatesResponse = MutableLiveData()
    }


}
