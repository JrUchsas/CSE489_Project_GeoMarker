package com.example.geomarker

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.geomarker.viewmodel.MapViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class MapFragment : Fragment() {
    private lateinit var mapView: MapView
    private var myLocationOverlay: MyLocationNewOverlay? = null
    private val viewModel: MapViewModel by viewModels()
    private val LOCATION_PERMISSION_REQUEST = 101

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)
        mapView = view.findViewById(R.id.mapView)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Initialize osmdroid config
        Configuration.getInstance().load(requireContext(), requireContext().getSharedPreferences("osmdroid", 0))
        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
        mapView.setMultiTouchControls(true)
        val mapController = mapView.controller
        mapController.setZoom(7.0)
        mapController.setCenter(GeoPoint(23.6850, 90.3563)) // Bangladesh

        // Show real-time location
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            enableMyLocation()
        } else {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST)
        }

        view.findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fabAddEntity).setOnClickListener {
            // Navigate to EntityFormFragment
            findNavController().navigate(R.id.entityFormFragment)
        }

        val mapEventsOverlay = org.osmdroid.views.overlay.MapEventsOverlay(object : org.osmdroid.events.MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: org.osmdroid.util.GeoPoint?): Boolean {
                // Close all info windows
                mapView.overlays.forEach {
                    if (it is org.osmdroid.views.overlay.Marker) {
                        it.closeInfoWindow()
                    }
                }
                return true
            }

            override fun longPressHelper(p: org.osmdroid.util.GeoPoint?): Boolean {
                return false
            }
        })
        mapView.overlays.add(1, mapEventsOverlay)

        viewModel.entities.observe(viewLifecycleOwner) { entities ->

            mapView.overlays.removeAll { it != myLocationOverlay && it != mapEventsOverlay }
            entities.forEach { entity ->
                val marker = Marker(mapView)
                marker.position = GeoPoint(entity.lat, entity.lon)
                marker.title = entity.title
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                marker.infoWindow = CustomMarkerInfoWindow(mapView)
                marker.relatedObject = entity

                mapView.overlays.add(marker)
            }
            mapView.invalidate()
        }
        
    }

    private fun enableMyLocation() {
        if (myLocationOverlay == null) {
            myLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(requireContext()), mapView)
            myLocationOverlay?.enableMyLocation()
            mapView.overlays.add(0, myLocationOverlay)
        }
    }

    @Suppress("DEPRECATION")
    @Deprecated("onRequestPermissionsResult is deprecated, but required for compatibility.")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            enableMyLocation()
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
        myLocationOverlay?.enableMyLocation()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
        myLocationOverlay?.disableMyLocation()
    }
}