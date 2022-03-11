package com.example.geofenceapp.ui.maps

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.geofenceapp.R
import com.example.geofenceapp.databinding.FragmentMapsBinding
import com.example.geofenceapp.util.ExtensionFunctions.hide
import com.example.geofenceapp.util.ExtensionFunctions.show
import com.example.geofenceapp.viewmodels.SharedViewModel
import com.google.android.gms.maps.*

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MapsFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: SharedViewModel by activityViewModels()

    private lateinit var map: GoogleMap


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMapsBinding.inflate(inflater, container, false)

        binding.addGeofenceFab.setOnClickListener {
            findNavController().navigate(R.id.action_mapsFragment_to_add_geofence2)
        }
        binding.historyFab.setOnClickListener {
            findNavController().navigate(R.id.action_mapsFragment_to_geofenceFragment)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.mapstyle))
        map.isMyLocationEnabled = true
        map.uiSettings.apply {
            isMapToolbarEnabled = false
            isMyLocationButtonEnabled = false
        }
        onGeofenceReady()

    }

    private fun onGeofenceReady() {
        if (sharedViewModel.geofenceReady) {
            sharedViewModel.geofenceReady = false
            displayInfoMessage()
            zoomToSelectedLocation()
        }
    }


    private fun displayInfoMessage() {
        lifecycleScope.launch {
            binding.pressToAddGeofenceTextView.show()
            delay(2000)
            binding.pressToAddGeofenceTextView.animate().alpha(0f).duration = 800
            delay(1000)
            binding.pressToAddGeofenceTextView.hide()
        }
    }

    private fun zoomToSelectedLocation() {
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(sharedViewModel.geoLatLong, 10f),
            2000,
            null
        )

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}