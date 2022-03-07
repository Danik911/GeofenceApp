package com.example.geofenceapp.ui.addgeofence

import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.geofenceapp.R
import com.example.geofenceapp.databinding.FragmentStep1Binding
import com.example.geofenceapp.viewmodels.SharedViewModel
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient

class Step1Fragment : Fragment() {

    private var _binding: FragmentStep1Binding? = null
    private val binding get() = _binding!!


    private val viewModel: SharedViewModel by activityViewModels()

    private lateinit var geoCoder: Geocoder
    private lateinit var placesClient: PlacesClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Places.initialize(requireContext(), "Your API key")
        placesClient = Places.createClient(requireContext())
        geoCoder = Geocoder(requireContext())
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentStep1Binding.inflate(inflater, container, false)

        binding.backTextView.setOnClickListener {
            onBackClicked()
        }
        getCountriesCode()





        return binding.root
    }

    private fun getCountriesCode() {
        //TODO("Not yet implemented")
    }

    private fun onBackClicked() {
        findNavController().navigate(R.id.action_global_mapsFragment2)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}