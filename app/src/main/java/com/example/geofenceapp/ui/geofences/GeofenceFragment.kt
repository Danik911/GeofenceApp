package com.example.geofenceapp.ui.geofences

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.geofenceapp.R
import com.example.geofenceapp.adaters.GeofencesRecyclerViewAdapter
import com.example.geofenceapp.databinding.FragmentGeofencesBinding
import com.example.geofenceapp.viewmodels.SharedViewModel

@RequiresApi(Build.VERSION_CODES.S)
class GeofenceFragment : Fragment() {

    private var _binding: FragmentGeofencesBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val geofencesAdapter by lazy {
       GeofencesRecyclerViewAdapter(sharedViewModel)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentGeofencesBinding.inflate(inflater, container, false)

        setupRecyclerView()
        observeDatabase()
        setupBackButton()

        return binding.root
    }

    private fun setupBackButton() {
        binding.toolbar.setNavigationOnClickListener {
           requireActivity().onBackPressed()
        }
    }


    private fun setupRecyclerView() {
        binding.geofencesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.geofencesRecyclerView.adapter = geofencesAdapter
    }

    private fun observeDatabase() {
        sharedViewModel.allGeofences.observe(viewLifecycleOwner) {
            geofencesAdapter.setData(it)
            binding.geofencesRecyclerView.scheduleLayoutAnimation()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}