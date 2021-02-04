package com.example.driverapp.datasource.retrofit

import com.example.driverapp.datasource.models.PlaceAutoCompleteResponse
import com.example.driverapp.datasource.models.PlaceDetailsResponse
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import javax.inject.Inject

class APIServiceImpl
@Inject
constructor(
    private val apiService: APIService
) {

    suspend fun getAutoCompletePlaces(
        input: String
    ): PlaceAutoCompleteResponse = withContext(IO) {
        apiService.getAutoCompletePlacesAsync(input = input)//.await()
    }

    suspend fun getPlaceDetails(
        placeId: String
    ): PlaceDetailsResponse = withContext(IO) {
        apiService.getPlaceDetailsAsync(placeID = placeId).await()
    }

}