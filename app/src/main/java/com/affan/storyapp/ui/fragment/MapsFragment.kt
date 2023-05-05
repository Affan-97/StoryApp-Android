package com.affan.storyapp.ui.fragment

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.affan.storyapp.R
import com.affan.storyapp.databinding.FragmentMapsBinding
import com.affan.storyapp.entity.ListStoryItem
import com.affan.storyapp.preferences.LoginPreference
import com.affan.storyapp.viewmodel.LoginFactory
import com.affan.storyapp.viewmodel.LoginViewModel
import com.affan.storyapp.viewmodel.StoryViewModel
import com.affan.storyapp.viewmodel.ViewModelFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

class MapsFragment : Fragment() {
    private lateinit var mMap: GoogleMap
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var factory: ViewModelFactory
    private lateinit var binding: FragmentMapsBinding
    private lateinit var storyViewModel: StoryViewModel
    private val boundsBuilder = LatLngBounds.Builder()
    private val callback = OnMapReadyCallback { googleMap ->
        mMap = googleMap


        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
        getMyLocation()
        setMapStyle()
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                        requireActivity().applicationContext,
                        R.raw.map_style
                    )
                )
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            getMyLocation()
        }
    }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                requireActivity().applicationContext, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
        val dataStore = requireContext().applicationContext.dataStore
        val pref = LoginPreference.getInstance(dataStore)
        loginViewModel = ViewModelProvider(this, LoginFactory(pref))[LoginViewModel::class.java]
        factory = ViewModelFactory.getInstance(requireActivity().applicationContext)
        storyViewModel = ViewModelProvider(this, factory)[StoryViewModel::class.java]
        loginViewModel.getLoginSession().observe(viewLifecycleOwner) {
            if (it != null) {
                it.token?.let { it1 -> storyViewModel.getStoryLoc(it1) }
            }
            storyViewModel.listStory.observe(viewLifecycleOwner) { list ->
                if (list != null) {
                    addMarkers(list)
                }
            }
        }
    }

    fun addMarkers(list: List<ListStoryItem>) {
        list.forEach {
            val latLng = LatLng(it.lat, it.lon)
            mMap.addMarker(
                MarkerOptions().position(latLng).title(it.name).snippet(it.description).icon(
                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                )
            )
            boundsBuilder.include(latLng)
        }
        val bounds: LatLngBounds = boundsBuilder.build()
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds,
                resources.displayMetrics.widthPixels,
                resources.displayMetrics.heightPixels,
                300
            )
        )
    }

    companion object {
        const val TAG = "MapsFragment"
    }

}