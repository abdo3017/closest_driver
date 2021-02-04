package com.example.driverapp.datasource.repository

import com.example.driverapp.datasource.firebase.FireBaseService
import com.example.driverapp.datasource.models.UserLocation
import com.example.driverapp.utils.ResponseStatusCallbacks
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class FireBaseRepository
@Inject
constructor(
    private val fireBaseService: FireBaseService
) {
    suspend fun getSourceLocations(): Flow<ResponseStatusCallbacks<List<UserLocation>>> = flow {
        emit(ResponseStatusCallbacks.loading(data = null))
        emit(ResponseStatusCallbacks.success(fireBaseService.getSourceLocations().documents.mapNotNull {
            it.toObject(UserLocation::class.java)
        }))
    }.catch {
        emit(ResponseStatusCallbacks.error(data = null, message = "Something went wrong!"))
    }

    suspend fun getDrivers(): Flow<ResponseStatusCallbacks<List<UserLocation>>> = flow {
        emit(ResponseStatusCallbacks.loading(data = null))
        emit(ResponseStatusCallbacks.success(fireBaseService.getSourceLocations().documents.mapNotNull {
            it.toObject(UserLocation::class.java)
        }))
    }.catch {
        emit(ResponseStatusCallbacks.error(data = null, message = "Something went wrong!"))
    }

}