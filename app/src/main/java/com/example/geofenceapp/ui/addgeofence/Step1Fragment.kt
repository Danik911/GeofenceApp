package com.example.geofenceapp.ui.addgeofence

import android.annotation.SuppressLint
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.geofenceapp.R
import com.example.geofenceapp.databinding.FragmentStep1Binding
import com.example.geofenceapp.viewmodels.SharedViewModel
import com.example.geofenceapp.viewmodels.Step1ViewModel
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.launch
import java.io.IOException

class Step1Fragment : Fragment() {

    private var _binding: FragmentStep1Binding? = null
    private val binding get() = _binding!!


    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val step1ViewModel: Step1ViewModel by viewModels()

    private lateinit var geoCoder: Geocoder
    private lateinit var placesClient: PlacesClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Places.initialize(requireContext(), getString(R.string.api_key))
        placesClient = Places.createClient(requireContext())
        geoCoder = Geocoder(requireContext())
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentStep1Binding.inflate(inflater, container, false)
        binding.sharedViewModel = sharedViewModel
        binding.step1ViewModel = step1ViewModel
        binding.lifecycleOwner = this

        binding.backTextView.setOnClickListener {
            onBackClicked()
        }
        getCountryCodeFromCurrentLocation()





        return binding.root
    }

    @SuppressLint("MissingPermission")
    private fun getCountryCodeFromCurrentLocation() {
        lifecycleScope.launch {
            val placesFields = listOf(Place.Field.LAT_LNG)
            val request: FindCurrentPlaceRequest = FindCurrentPlaceRequest.newInstance(placesFields)

            val placeResponse = placesClient.findCurrentPlace(request)
            placeResponse.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                  try{
                      val response = task.result
                      val latLng = response.placeLikelihoods[0].place.latLng!!
                      val address = geoCoder.getFromLocation(
                          latLng.latitude,
                          latLng.longitude,
                          1
                      )
                      sharedViewModel.geoCountryCode = address[0].countryCode

                      Log.d("Step1Fragment", sharedViewModel.geoCountryCode)
                  } catch (exception: IOException){
                      Log.d("Exception", "Country's code can't be fetched")
                  } finally {
                      enableNextButton()
                  }
                } else {
                    val exception = task.exception
                    if (exception is ApiException) {
                        Log.e("Step1Fragment", exception.statusCode.toString())
                    }
                    enableNextButton()

                }
            }
        }
    }

    private fun onBackClicked() {
        findNavController().navigate(R.id.action_global_mapsFragment)
    }

    private fun enableNextButton() {
        if (sharedViewModel.geoName.isNotEmpty()) {
            step1ViewModel.enableNextButton(true)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}