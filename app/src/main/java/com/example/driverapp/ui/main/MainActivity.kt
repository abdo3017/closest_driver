package com.example.driverapp.ui.main

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import com.example.driverapp.R
import com.example.driverapp.databinding.ActivityMainBinding
import com.example.driverapp.listner.PlacesListeners
import com.example.driverapp.ui.base.BaseActivity
import com.example.driverapp.ui.search.SearchActivity
import com.example.driverapp.utils.ResponseStatus
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(), OnMapReadyCallback {
    private lateinit var viewModel: MapViewModel
    private lateinit var mGoogleMap: GoogleMap

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MapViewModel::class.java)

        mapView.onCreate(savedInstanceState)
        MapsInitializer.initialize(this)
        mapView.getMapAsync(this)

        getViewDataBinding().etDestinationLocation.isFocusableInTouchMode = false
        getViewDataBinding().etSourceLocation.isFocusableInTouchMode = false

        getViewDataBinding().listener = object : PlacesListeners {
            override fun onTapGoToPlacesScreen(view: View) {
                val intent = Intent(this@MainActivity, SearchActivity::class.java)
                Log.d("trtrtrtrt", R.id.etSourceLocation.toString())

                intent.putExtra("textId", view.id)
                startActivityForResult(intent, 1)
            }

        }

        observeData()
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    private fun getPlaceDetails(s: String) {
        viewModel.getPlaceDetails(s)
    }


    private fun observeData() {
        viewModel.dataPlaceDetailsResponse.observe(this, {
            it?.let {
                when (it.status) {
                    ResponseStatus.SUCCESS -> {
                        if (it.data != null) {
                            val location = it.data.result?.geometry?.location
                            animateCamera(location?.lat, location?.lng, it.data.result?.name!!)
                        }
                    }
                    ResponseStatus.LOADING -> {
                        Log.d("trtrtrtrt", "heheheheheheh")
                    }
                    ResponseStatus.ERROR -> {

                    }
                }
            }
        })
    }

    override fun onMapReady(map: GoogleMap?) {
        map?.let {
            mGoogleMap = map
            mGoogleMap.clear()
            formatMap()
        }
    }

    private fun animateCamera(lat: Double?, lng: Double?, placeName: String = "") {
        mGoogleMap.clear()
        mGoogleMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    lat!!,
                    lng!!
                ), 15f
            )
        )
        val markerOption =
            MarkerOptions().apply {
                title(placeName)
                position(LatLng(lat, lng))
            }
        mGoogleMap.addMarker(markerOption)
    }

    private fun formatMap() {
        mGoogleMap.apply {
            isBuildingsEnabled = false
            uiSettings.isMapToolbarEnabled = false
            uiSettings.isMyLocationButtonEnabled = false
            uiSettings.isCompassEnabled = false
        }
    }

    override fun onResume() {
        if (mapView != null) mapView.onResume()
        super.onResume()
    }

    override fun onPause() {
        if (mapView != null) mapView.onPause()
        super.onPause()
    }

    override fun onStop() {
        if (mapView != null) mapView.onStop()
        super.onStop()
    }

    override fun onDestroy() {
        if (mapView != null) mapView.onDestroy()
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {
                if (data != null && data.extras != null && data.extras!!.containsKey("placeId")) {
                    val placeId = data.getStringExtra("placeId")
                    getPlaceDetails(placeId!!)
                }
            }
        }
    }
}