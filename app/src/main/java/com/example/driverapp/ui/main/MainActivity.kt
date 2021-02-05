package com.example.driverapp.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import com.example.driverapp.R
import com.example.driverapp.databinding.ActivityMainBinding
import com.example.driverapp.datasource.models.UserLocation
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
import com.google.android.gms.maps.model.PolylineOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import java.util.*

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(), OnMapReadyCallback {
    private lateinit var viewModel: MapViewModel
    private lateinit var mGoogleMap: GoogleMap
    private lateinit var geocoder: Geocoder
    private lateinit var sourceLocation: UserLocation
    private lateinit var destinationLocation: UserLocation

    private var latitudeSource: Double? = -1.0
    private var longitudeSource: Double? = -1.0
    private var latitudeDestination: Double? = -1.0
    private var longitudeDestination: Double? = -1.0
    private lateinit var city: String
    private lateinit var address: String
    private lateinit var destinationName: String
    private var changeLocation = false
    private lateinit var drivers: List<UserLocation>
    private var filteredDrivers: MutableList<UserLocation> = mutableListOf()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MapViewModel::class.java)
        setViews(savedInstanceState)
        getDrivers()
        observeData()
    }

    private fun getDrivers() {
        viewModel.getDrivers()
    }

    private fun setViews(savedInstanceState: Bundle?) {
        mapView.onCreate(savedInstanceState)
        MapsInitializer.initialize(this)
        mapView.getMapAsync(this)
        getViewDataBinding().drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

        getViewDataBinding().etDestinationLocation.isFocusableInTouchMode = false
        getViewDataBinding().etSourceLocation.isFocusableInTouchMode = false
        getViewDataBinding().btnRequest.setOnClickListener {
            getClosestDrivers()
        }
        getViewDataBinding().imgHamburger.setOnClickListener {
            getViewDataBinding().drawerLayout.openDrawer(GravityCompat.START)
        }
        getViewDataBinding().listener = object : PlacesListeners {
            override fun onTapGoToPlacesScreen(view: View) {
                val intent = Intent(this@MainActivity, SearchActivity::class.java)
                intent.putExtra("textId", view.id)
                startActivityForResult(intent, 1)
                overridePendingTransition(0, 0)
            }
        }
    }

    private fun getClosestDrivers() {
        val locSource = Location("")
        locSource.latitude = latitudeSource!!
        locSource.longitude = longitudeSource!!

        val locDestination = Location("")
        locDestination.latitude = latitudeDestination!!
        locDestination.longitude = longitudeDestination!!

        val distanceInMeters = locSource.distanceTo(locDestination)
        val loc = Location("")
        drivers.forEach {
            loc.latitude = it.latitude!!.toDouble()
            loc.longitude = it.longitude!!.toDouble()
            if (locSource.distanceTo(loc) <= distanceInMeters / 2) {
                filteredDrivers.add(it)
                Log.d("filteredDrivers", it.toString())
                Toast.makeText(this, it.name, Toast.LENGTH_SHORT).show()
                print(it)
            }
        }
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
                            destinationLocation = UserLocation(
                                latitude = it.data.result?.geometry?.location!!.lat.toString(),
                                longitude = it.data.result.geometry?.location!!.lng.toString(),
                                name = destinationName
                            )
                            latitudeDestination = destinationLocation.latitude!!.toDouble()
                            longitudeDestination = destinationLocation.longitude!!.toDouble()
                            animateCamera(location?.lat, location?.lng, it.data.result.name!!)
                            if (latitudeDestination != -1.0 && latitudeSource != -1.0 && longitudeDestination != -1.0 && longitudeSource != -1.0) {
                                drawLine()
                            }
                        }
                    }
                    ResponseStatus.LOADING -> {
                    }
                    ResponseStatus.ERROR -> {

                    }
                }
            }
        })
        viewModel.dataStateUserDrivers.observe(this, {
            it?.let {
                when (it.status) {
                    ResponseStatus.ERROR -> {
                    }
                    ResponseStatus.LOADING -> {
                    }
                    ResponseStatus.SUCCESS -> {
                        drivers = it.data as ArrayList<UserLocation>
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
        changeLocation = true
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
            if (ActivityCompat.checkSelfPermission(
                    this@MainActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this@MainActivity,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                setPermission()
            } else
                isMyLocationEnabled = true
            setOnMyLocationChangeListener {
                if (!changeLocation) {
                    latitudeSource = it.latitude
                    longitudeSource = it.longitude
                    try {
                        geocoder = Geocoder(this@MainActivity, Locale.getDefault())
                        val addresses: List<Address> = geocoder.getFromLocation(
                            latitudeSource!!, longitudeSource!!, 1
                        )
                        address = addresses[0].getAddressLine(0)
                        city = addresses[0].locality
                        animateCamera(it.latitude, it.longitude, city)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    private fun drawLine() {
        mGoogleMap.addPolyline(
            PolylineOptions()
                .add(
                    LatLng(latitudeSource!!, longitudeSource!!),
                    LatLng(
                        latitudeDestination!!,
                        longitudeDestination!!
                    )
                )
                .width(5F)
                .color(Color.RED)
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                if (data != null && data.extras != null && data.extras!!.containsKey("placeId")) {
                    val placeId = data.getStringExtra("placeId")
                    destinationName = data.getStringExtra("description")!!
                    getViewDataBinding().etDestinationLocation.setText(destinationName)
                    getPlaceDetails(placeId!!)
                }
            }
        } else if (resultCode == RESULT_CANCELED) {
            if (requestCode == 1) {
                if (data != null && data.extras != null && data.extras!!.containsKey("sourceLocation")) {
                    sourceLocation =
                        (data.extras!!.getParcelable("sourceLocation") as UserLocation?)!!
                    getViewDataBinding().etSourceLocation.setText(sourceLocation.name)
                    latitudeSource = sourceLocation.latitude!!.toDouble()
                    longitudeSource = sourceLocation.longitude!!.toDouble()

                    animateCamera(
                        sourceLocation.latitude!!.toDouble(),
                        sourceLocation.longitude!!.toDouble(),
                        sourceLocation.name!!
                    )
                    if (latitudeDestination != -1.0 && latitudeSource != -1.0 && longitudeDestination != -1.0 && longitudeSource != -1.0) {
                        drawLine()
                    }

                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (grantResults.isNotEmpty()
            && grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            //If user presses allow
            if (ActivityCompat.checkSelfPermission(
                    this@MainActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this@MainActivity,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            )
                mGoogleMap.isMyLocationEnabled = true
            formatMap()
        } else {
            //If user presses deny
            Log.d("check", "permission denied")

        }
    }

    @SuppressLint("MissingPermission")
    private fun setPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                1
            )
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

}
