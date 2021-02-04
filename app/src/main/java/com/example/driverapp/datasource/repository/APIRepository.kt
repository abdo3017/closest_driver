package com.example.driverapp.datasource.repository

import com.example.driverapp.datasource.models.PlaceAutoCompleteResponse
import com.example.driverapp.datasource.models.PlaceDetailsResponse
import com.example.driverapp.datasource.retrofit.APIServiceImpl
import com.example.driverapp.utils.ResponseStatusCallbacks
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class APIRepository
@Inject
constructor(
    private val apiServiceImpl: APIServiceImpl
) {
    suspend fun getPlacesFromAutocomplete(input: String): Flow<ResponseStatusCallbacks<PlaceAutoCompleteResponse>> =
        flow {
            emit(ResponseStatusCallbacks.loading(data = null))
            ResponseStatusCallbacks.success(apiServiceImpl.getAutoCompletePlaces(input))
        }.catch {
            ResponseStatusCallbacks.error(data = null, message = "Something went wrong!")
        }

    suspend fun getPlaceDetails(placeId: String): Flow<ResponseStatusCallbacks<PlaceDetailsResponse>> =
        flow {
            emit(ResponseStatusCallbacks.loading(data = null))
            ResponseStatusCallbacks.success(apiServiceImpl.getPlaceDetails(placeId))
        }.catch {
            ResponseStatusCallbacks.error(data = null, message = "Something went wrong!")
        }

}