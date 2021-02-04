package com.example.driverapp.ui.main

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.driverapp.datasource.models.PlaceDetailsResponse
import com.example.driverapp.datasource.models.UserLocation
import com.example.driverapp.datasource.repository.APIRepository
import com.example.driverapp.datasource.repository.FireBaseRepository
import com.example.driverapp.utils.ResponseStatusCallbacks
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class MapViewModel
@ViewModelInject
constructor(
    private val fireBaseRepository: FireBaseRepository,
    private val apiRepository: APIRepository
) : ViewModel() {
    private val _dataStateDrivers: MutableLiveData<ResponseStatusCallbacks<List<UserLocation>>> =
        MutableLiveData()
    val dataStateUserDrivers: LiveData<ResponseStatusCallbacks<List<UserLocation>>>
        get() = _dataStateDrivers

    private var _dataStatePlaceDetailsResponse =
        MutableLiveData<ResponseStatusCallbacks<PlaceDetailsResponse>>()
    val dataPlaceDetailsResponse: LiveData<ResponseStatusCallbacks<PlaceDetailsResponse>>
        get() = _dataStatePlaceDetailsResponse


    fun getPlaceDetails(placeId: String) {
        viewModelScope.launch {
            apiRepository.getPlaceDetails(placeId).onEach {
                _dataStatePlaceDetailsResponse.value = it
            }.launchIn(viewModelScope)
        }
    }

    fun getDrivers() {
        viewModelScope.launch {
            fireBaseRepository.getDrivers().onEach {
                _dataStateDrivers.value = it
            }.launchIn(viewModelScope)
        }
    }
}