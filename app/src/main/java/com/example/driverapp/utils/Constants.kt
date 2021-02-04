package com.example.driverapp.utils

import com.example.driverapp.R
import com.example.driverapp.ui.main.MyApplication

/**
 * Created by Mustufa Ansari on 10/12/2020.
 * Email : mustufaayub82@gmail.com
 */
object Constants {
    const val GOOGLE_PLACE_AUTOCOMPLETE = "/maps/api/place/autocomplete/json"
    const val GOOGLE_PLACE_DETAILS = "/maps/api/place/details/json"
    const val HEADER_ACCEPT_ENCODING = "Accept-Encoding: identity"
    const val REMOTE_URL = "https://maps.googleapis.com/"
    const val PLACE_AUTOCOMPLETE_RADIUS = "3500"
    val API_KEY = MyApplication.instance!!.resources.getString(R.string.google_maps_key)
    const val PLACE_AUTOCOMPLETE_COMPONENT = "country:eg"


}