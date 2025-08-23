package com.example.bestbooker.data.viewmodel

import androidx.lifecycle.*
import com.example.bestbooker.data.model.UberFareResponse
import com.example.bestbooker.data.network.NetworkModule
import com.example.bestbooker.data.repository.UberRepository
import kotlinx.coroutines.launch

class FareViewModel : ViewModel() {
    private val repository = UberRepository(NetworkModule.provideUberApi())

    private val _fares = MutableLiveData<List<UberFareResponse>>()
    val fares: LiveData<List<UberFareResponse>> = _fares

    fun getFares(startLat: Double, startLng: Double, endLat: Double, endLng: Double, token: String) {
        viewModelScope.launch {
            val result = repository.fetchFareEstimate(startLat, startLng, endLat, endLng, token)
            _fares.postValue(result ?: emptyList())
        }
    }
}