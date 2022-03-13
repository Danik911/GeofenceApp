package com.example.geofenceapp.viewmodels

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
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
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.SphericalUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.sqrt

@HiltViewModel
class SharedViewModel @Inject constructor(
    application: Application,
    private val dataStoreRepository: DataStoreRepository,
    private val geofenceRepository: GeofenceRepository
) : AndroidViewModel(application) {

    val app = application
    var geoCountryCode: String = ""
    var geoName: String = "Default name"
    var geoId: Long = 0L
    var geoLocationName: String = "Search a City"
    var geoLatLong: LatLng = LatLng(0.0, 0.0)
    var geoRadius: Float = 500f
    var geoSnapshot: Bitmap? = null

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

    fun insertGeofence(geofenceEntity: GeofenceEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            geofenceRepository.insertGeofence(geofenceEntity)
        }
    }

    fun deleteGeofence(geofenceEntity: GeofenceEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            geofenceRepository.deleteGeofence(geofenceEntity)
        }
    }

    fun addGeofenceToDatabase(location: LatLng) {
        val geofence = GeofenceEntity(
            geoId,
            geoName,
            geoLocationName,
            location.latitude,
            location.longitude,
            geoRadius,
            geoSnapshot!!
        )
        insertGeofence(geofence)
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

    fun getBounds(center: LatLng, radius: Float): LatLngBounds {
        val distanceFromCenterToCorner = radius * sqrt(2.0)
        val southWestCorner =
            SphericalUtil.computeOffset(center, distanceFromCenterToCorner, 225.0)
        val northEastCorner =
            SphericalUtil.computeOffset(center, distanceFromCenterToCorner, 45.0)
        return LatLngBounds(southWestCorner, northEastCorner)
    }

}