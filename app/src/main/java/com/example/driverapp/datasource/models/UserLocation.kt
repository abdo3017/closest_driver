package com.example.driverapp.datasource.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserLocation(
    var id: String? = null,
    var latitude: String? = null,
    var longitude: String? = null,
    var name: String? = null
) : Parcelable