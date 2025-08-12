package com.example.locmark

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import com.example.locmark.viewmodel.MapViewModel

class MapFragment : Fragment() {
    private lateinit var mapView: MapView
    private val viewModel: MapViewModel by viewModels()

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
        mapView.setMultiTouchControls(true)
        val mapController = mapView.controller
        mapController.setZoom(7.0)
        mapController.setCenter(GeoPoint(23.6850, 90.3563)) // Bangladesh

        viewModel.entities.observe(viewLifecycleOwner) { entities ->
            mapView.overlays.clear()
            entities.forEach { entity ->
                val marker = Marker(mapView)
                marker.position = GeoPoint(entity.lat, entity.lon)
                marker.title = entity.title
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                mapView.overlays.add(marker)
            }
            mapView.invalidate()
        }
        viewModel.fetchEntities()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }
}
