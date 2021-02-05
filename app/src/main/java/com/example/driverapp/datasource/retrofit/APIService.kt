package com.example.driverapp.datasource.retrofit

import com.example.driverapp.datasource.models.PlaceAutoCompleteResponse
import com.example.driverapp.datasource.models.PlaceDetailsResponse
import com.example.driverapp.utils.Constants.API_KEY
import com.example.driverapp.utils.Constants.GOOGLE_PLACE_AUTOCOMPLETE
import com.example.driverapp.utils.Constants.GOOGLE_PLACE_DETAILS
import com.example.driverapp.utils.Constants.HEADER_ACCEPT_ENCODING
import com.example.driverapp.utils.Constants.PLACE_AUTOCOMPLETE_COMPONENT
import com.example.driverapp.utils.Constants.PLACE_AUTOCOMPLETE_RADIUS
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query


interface APIService {

    /**
     * get places by query
     */
    @Headers(HEADER_ACCEPT_ENCODING)
    @GET(GOOGLE_PLACE_AUTOCOMPLETE)
    suspend fun getAutoCompletePlacesAsync(
        @Query("input") input: String?,
        @Query("components") component: String = PLACE_AUTOCOMPLETE_COMPONENT,
        @Query("radius") radius: String = PLACE_AUTOCOMPLETE_RADIUS,
        @Query("key") googleMapApiKey: String = API_KEY
    ): PlaceAutoCompleteResponse

    /**
     * get place's details
     */
    @Headers(HEADER_ACCEPT_ENCODING)
    @GET(GOOGLE_PLACE_DETAILS)
    suspend fun getPlaceDetailsAsync(
        @Query("place_id") placeID: String?,
        @Query("key") key: String = API_KEY
    ): PlaceDetailsResponse

}