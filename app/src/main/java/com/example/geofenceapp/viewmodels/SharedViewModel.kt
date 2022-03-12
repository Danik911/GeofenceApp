package com.example.geofenceapp.viewmodels

import android.app.Application
import android.content.Context
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.geofenceapp.data.DataStoreRepository
import com.example.geofenceapp.data.GeofenceEntity
import com.example.geofenceapp.data.GeofenceRepository
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    application: Application,
    private val dataStoreRepository: DataStoreRepository,
    private val geofenceRepository: GeofenceRepository
) : AndroidViewModel(application) {

    val app = application
    var geoCountryCode = ""
    var geoName = "Default name"
    var geoId = 0L
    var geoLocationName = "Search a City"
    var geoLatLong = LatLng(0.0, 0.0)
    var geoRadius = 500f

    var geoCitySelected = false
    var geofenceReady = false
    var geofencePrepared = false

    //DataStore

    val firstLaunch = dataStoreRepository.readFirstLaunch.asLiveData()

    fun saveFirstLaunch(firstLaunch: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.saveFirstLaunch(firstLaunch)
        }
    }

    //Database

    val allGeofences = geofenceRepository.allGeofences.asLiveData()

    fun insertGeofence(geofenceEntity: GeofenceEntity){
        viewModelScope.launch(Dispatchers.IO) {
            geofenceRepository.insertGeofence(geofenceEntity)
        }
    }
    fun deleteGeofence(geofenceEntity: GeofenceEntity){
        viewModelScope.launch(Dispatchers.IO) {
            geofenceRepository.deleteGeofence(geofenceEntity)
        }
    }


    fun checkDeviceLocationSettings(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val locationManager =
                context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            locationManager.isLocationEnabled
        } else {
            val mode: Int = Settings.Secure.getInt(
                context.contentResolver,
                Settings.Secure.LOCATION_MODE,
                Settings.Secure.LOCATION_MODE_OFF
            )
            mode != Settings.Secure.LOCATION_MODE_OFF
        }
    }

}