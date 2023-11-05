package com.abid.storyapp.view.maps

import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import com.abid.storyapp.R
import com.abid.storyapp.data.response.ListStoryItem

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.abid.storyapp.databinding.ActivityMapsBinding
import com.abid.storyapp.view.ViewModelFactory
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private val mapViewModel by viewModels<MapsViewModel>{
        ViewModelFactory(applicationContext)
    }

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        val stories = mapViewModel.getStoryLocation()
        if(stories) Toast.makeText(this@MapsActivity, "Cannot retrieve story data", Toast.LENGTH_SHORT).show()
        mapViewModel.listLocation.observe(this){ data ->
            setLocation(data)
        }


        setMapStyle()
    }

    private fun setLocation(story: List<ListStoryItem?>?){
        val data = listOf(story)
        data.forEach{ data ->
            data?.forEach{ data ->
                val latLng = data?.lat?.let { data.lon?.let { it1 -> LatLng(it, it1) } }
                latLng?.let {
                    MarkerOptions()
                        .position(it)
                        .title(data.name)
                        .snippet(data.description)
                }?.let {
                    mMap.addMarker(
                        it
                    )
                }
            }
        }
    }

    private fun setMapStyle(){
        try {
            val success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success){
                Log.e(TAG, "Style parsing failed")
            }
        }catch (exception: Resources.NotFoundException){
            Log.e(TAG, "Can't find style, Error: ", exception)
        }
    }

    companion object{
        private const val TAG = "MapsActivity"
    }
}