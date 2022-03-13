package com.example.geofenceapp.ui.maps

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.geofenceapp.R
import com.example.geofenceapp.databinding.FragmentMapsBinding
import com.example.geofenceapp.util.ExtensionFunctions.hide
import com.example.geofenceapp.util.ExtensionFunctions.show
import com.example.geofenceapp.util.Permissions
import com.example.geofenceapp.util.requestBackgroundLocationPermission
import com.example.geofenceapp.viewmodels.SharedViewModel
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MapsFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMapLongClickListener,
    EasyPermissions.PermissionCallbacks, GoogleMap.SnapshotReadyCallback {

    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: SharedViewModel by activityViewModels()

    private lateinit var map: GoogleMap
    private lateinit var circle: Circle


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
        map.setOnMapLongClickListener(this)
        map.uiSettings.apply {
            isMapToolbarEnabled = false
            isMyLocationButtonEnabled = true
        }
        onGeofenceReady()
        drawAllGeofences()

    }

    private fun drawAllGeofences() {
        sharedViewModel.allGeofences.observe(viewLifecycleOwner) { allGeofences ->
            map.clear()
            for (geofence in allGeofences) {
                drawCircle(LatLng(geofence.latitude, geofence.longitude), geofence.radius)
                drawMarker(LatLng(geofence.latitude, geofence.longitude), geofence.name)
            }
        }
    }

    private fun onGeofenceReady() {
        if (sharedViewModel.geofenceReady) {
            sharedViewModel.geofenceReady = false
            sharedViewModel.geofencePrepared = true
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

    override fun onMapLongClick(location: LatLng) {
        if (Permissions.hasBackgroundLocationPermission(requireContext())) {
            if (sharedViewModel.geofencePrepared) {
                setupGeofence(location)
            } else {
                Toast.makeText(
                    requireContext(),
                    "You need to create a new Geofence first",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            requestBackgroundLocationPermission(this)
        }
    }

    private fun setupGeofence(location: LatLng) {
        lifecycleScope.launch {
            if (sharedViewModel.checkDeviceLocationSettings(requireContext())) {
                drawCircle(location, sharedViewModel.geoRadius)
                drawMarker(location, sharedViewModel.geoName)
                zoomToGeofence(circle.center, circle.radius.toFloat())

                delay(1500)
                map.snapshot(this@MapsFragment)
                delay(2000)
                sharedViewModel.addGeofenceToDatabase(location)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Please enable Location Settings",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    private fun drawMarker(location: LatLng, name: String) {
        map.addMarker(
            MarkerOptions()
                .position(location)
                .title(name)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
        )
    }

    private fun drawCircle(location: LatLng, radius: Float) {
        circle = map
            .addCircle(
                CircleOptions()
                    .center(location)
                    .radius(radius.toDouble())
                    .strokeColor(ContextCompat.getColor(requireContext(), R.color.blue_700))
                    .fillColor(ContextCompat.getColor(requireContext(), R.color.blue_transparent))
            )
    }

    private fun zoomToGeofence(center: LatLng, radius: Float) {
        map.animateCamera(
            CameraUpdateFactory.newLatLngBounds(
                sharedViewModel.getBounds(center, radius), 10
            ), 1000, null
        )
    }

    override fun onSnapshotReady(snapshot: Bitmap?) {
        sharedViewModel.geoSnapshot = snapshot
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            SettingsDialog.Builder(requireActivity()).build().show()
        } else {
            requestBackgroundLocationPermission(this)
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        onGeofenceReady()
        Toast.makeText(
            requireContext(),
            "Permission Granted! Long press on the Map to add a Geofence",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults,
            this
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}