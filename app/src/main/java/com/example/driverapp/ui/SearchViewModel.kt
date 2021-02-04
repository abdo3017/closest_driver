package com.example.driverapp.ui

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.driverapp.datasource.models.PlaceAutoCompleteResponse
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
class SearchViewModel
@ViewModelInject
constructor(
    private val fireBaseRepository: FireBaseRepository,
    private val apiRepository: APIRepository
) : ViewModel() {

    private val _dataStateUserLocation: MutableLiveData<ResponseStatusCallbacks<List<UserLocation>>> =
        MutableLiveData()
    val dataStateUserLocation: LiveData<ResponseStatusCallbacks<List<UserLocation>>>
        get() = _dataStateUserLocation

    private var _dataStateAutocompleteResult =
        MutableLiveData<ResponseStatusCallbacks<PlaceAutoCompleteResponse>>()
    val dataStateAutocompleteResult: LiveData<ResponseStatusCallbacks<PlaceAutoCompleteResponse>>
        get() = _dataStateAutocompleteResult

    private var _dataStatePlaceDetailsResponse =
        MutableLiveData<ResponseStatusCallbacks<PlaceDetailsResponse>>()
    val dataPlaceDetailsResponse: LiveData<ResponseStatusCallbacks<PlaceDetailsResponse>>
        get() = _dataStatePlaceDetailsResponse

    fun getSourceLocations() {
        viewModelScope.launch {
            fireBaseRepository.getSourceLocations().onEach {
                _dataStateUserLocation.value = it
            }.launchIn(viewModelScope)
        }
    }

    fun getPlacesFromAutocomplete(input: String) {
        viewModelScope.launch {
            apiRepository.getPlacesFromAutocomplete(input).onEach {
                _dataStateAutocompleteResult.value = it
            }.launchIn(viewModelScope)
        }
    }


    fun getPlaceDetails(placeId: String) {
        viewModelScope.launch {
            apiRepository.getPlaceDetails(placeId).onEach {
                _dataStatePlaceDetailsResponse.value = it
            }.launchIn(viewModelScope)
        }
    }
}