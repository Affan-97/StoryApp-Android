package com.affan.storyapp.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.affan.storyapp.R
import com.affan.storyapp.databinding.ActivityLocationBinding
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException
import java.util.*


class LocationActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityLocationBinding
    private var poiMarker: Marker? = null
    private var markerData: MarkerData? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.btnSelect.setOnClickListener {
            val latitude = markerData?.latitude
            val longitude = markerData?.longitude
            val addressName = markerData?.addressName
            val intent = Intent()
            intent.putExtra("EXTRA_LAT", latitude)
            intent.putExtra("EXTRA_LNG", longitude)
            intent.putExtra("ADDRESS", addressName)
            setResult(PostActivity.LOCATION_ACTIVITY_REQUEST_CODE, intent)
            finish()
        }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        mMap.setOnMapLongClickListener { pos ->
            poiMarker?.remove()

            poiMarker = mMap.addMarker(
                MarkerOptions()
                    .position(pos)
                    .title("location")
                    .snippet(getAddressName(pos.latitude, pos.longitude))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
            )
            poiMarker?.showInfoWindow()
            markerData = MarkerData(
                latitude = pos.latitude,
                longitude = pos.longitude,
                addressName = getAddressName(pos.latitude, pos.longitude)
            )
            if (markerData?.latitude != null && markerData?.longitude != null && markerData?.addressName != null) {
                // Make the submit button visible
                binding.btnSelect.visibility = View.VISIBLE
            } else {
                binding.btnSelect.visibility = View.GONE

            }
        }
        currentLoaction()
    }


    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                currentLoaction()
            }
        }

    private fun currentLoaction() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun getAddressName(lat: Double, lon: Double): String? {
        var addressName: String? = null
        val geocoder = Geocoder(this@LocationActivity, Locale.getDefault())
        try {
            val list = geocoder.getFromLocation(lat, lon, 1)
            if (list != null && list.size != 0) {
                addressName = list[0].getAddressLine(0)

            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return addressName
    }


}

data class MarkerData(
    val latitude: Double?,
    val longitude: Double?,
    val addressName: String?
)