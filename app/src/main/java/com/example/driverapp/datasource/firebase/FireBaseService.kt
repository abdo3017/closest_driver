package com.example.driverapp.datasource.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FireBaseService
@Inject
constructor(
    private val firestoreDB: FirebaseFirestore
) {

    suspend fun getSourceLocations(): QuerySnapshot =
        withContext(Dispatchers.IO) {
            firestoreDB.collection("Source").get().await()
        }

    suspend fun getDrivers(): QuerySnapshot =
        withContext(Dispatchers.IO) {
            firestoreDB.collection("Drivers").get().await()
        }
}