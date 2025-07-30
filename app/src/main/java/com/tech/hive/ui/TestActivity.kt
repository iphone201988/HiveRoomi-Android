package com.tech.hive.ui

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.tech.hive.R

class TestActivity : AppCompatActivity() {

    // Declare a variable for MapView
    private lateinit var mapView: MapView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Get the API Key by app's BuildConfig
        val key = "7EeVfge3KFVo7AlXanER"

        val mapId = "streets-v2"

        val styleUrl = "https://api.maptiler.com/maps/$mapId/style.json?key=$key"

        // Init MapLibre
        Mapbox.getInstance(this)

        // Init layout view
        val inflater = LayoutInflater.from(this)
        val rootView = inflater.inflate(R.layout.activity_test, null)
        setContentView(rootView)

        // Init the MapView
        mapView = rootView.findViewById(R.id.mapView)
        mapView.getMapAsync { map ->

            map.setStyle(styleUrl) { style ->

                // Set the camera position
                val latLng = LatLng(18.94018, 72.83484)
                map.cameraPosition = CameraPosition.Builder()
                    .target(latLng)
                    .zoom(14.0)
                    .build()

                // Add marker
                map.addMarker(
                    com.mapbox.mapboxsdk.annotations.MarkerOptions()
                        .position(latLng)
                        .title("My Location")
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

}