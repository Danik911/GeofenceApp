package com.example.geofenceapp.ui.addgeofence

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.geofenceapp.R
import com.example.geofenceapp.databinding.FragmentStep3Binding
import com.example.geofenceapp.viewmodels.SharedViewModel


class Step3Fragment : Fragment() {

    private var _binding: FragmentStep3Binding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: SharedViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding =  FragmentStep3Binding.inflate(layoutInflater, container, false)
        binding.sharedViewModel = sharedViewModel
        binding.lifecycleOwner = this

        binding.step3BackTextView.setOnClickListener {
            findNavController().navigate(R.id.action_step3Fragment_to_step2Fragment)
        }
        binding.step3DoneTextView.setOnClickListener {
            sharedViewModel.geofenceReady = true
            sharedViewModel.geoRadius = binding.slider.value
            findNavController().navigate(R.id.action_global_mapsFragment)
        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}