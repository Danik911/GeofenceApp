package com.example.geofenceapp.viewmodels

import android.annotation.SuppressLint
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.geofenceapp.broadcastreceiver.GeofenceBroadcastReceiver
import com.example.geofenceapp.data.DataStoreRepository
import com.example.geofenceapp.data.GeofenceEntity
import com.example.geofenceapp.data.GeofenceRepository
import com.example.geofenceapp.util.Permissions
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.SphericalUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CompletableDeferred
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
    private var geofencingClient =
        LocationServices.getGeofencingClient(app.applicationContext)
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
    var geofenceStopped = false

    //DataStore

    fun resetValues() {
        geoCountryCode = ""
        geoName = "Default name"
        geoId = 0L
        geoLocationName = "Search a City"
        geoLatLong = LatLng(0.0, 0.0)
        geoRadius = 500f
        geoSnapshot = null

        geoCitySelected = false
        geofenceReady = false
        geofencePrepared = false
        geofenceStopped = false
    }

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


    @RequiresApi(Build.VERSION_CODES.S)
    private fun setPendingIntent(geoId: Int): PendingIntent {
        val intent = Intent(app, GeofenceBroadcastReceiver::class.java)
        return PendingIntent.getBroadcast(
            app,
            geoId,
            intent,
            PendingIntent.FLAG_MUTABLE
        )
    }

    @RequiresApi(Build.VERSION_CODES.S)
    @SuppressLint("MissingPermission")
    fun startGeofence(
        latitude: Double,
        longitude: Double
    ) {
        if (Permissions.hasBackgroundLocationPermission(app)) {
            val geofence = Geofence.Builder()
                .setRequestId(geoId.toString())
                .setCircularRegion(
                    latitude,
                    longitude,
                    geoRadius
                )
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(
                    Geofence.GEOFENCE_TRANSITION_ENTER
                            or Geofence.GEOFENCE_TRANSITION_EXIT
                            or Geofence.GEOFENCE_TRANSITION_DWELL
                )
                .setLoiteringDelay(5000)
                .build()

            val geofencingRequest = GeofencingRequest.Builder()
                .setInitialTrigger(
                    GeofencingRequest.INITIAL_TRIGGER_ENTER
                            or GeofencingRequest.INITIAL_TRIGGER_EXIT
                            or GeofencingRequest.INITIAL_TRIGGER_DWELL
                )
                .addGeofence(geofence)
                .build()

            geofencingClient.addGeofences(geofencingRequest, setPendingIntent(geoId.toInt())).run {
                addOnSuccessListener {
                    Log.d("Geofence", "Geofence successfully added")
                }
                addOnFailureListener {
                    Log.d("Geofence", it.message.toString())
                }
            }

        } else {
            Log.d("Geofence", "Permission not granted")
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    suspend fun stopGeofence(geoIds: List<Long>): Boolean {
        return if (Permissions.hasBackgroundLocationPermission(app)) {
            val result = CompletableDeferred<Boolean>()
            geofencingClient.removeGeofences(setPendingIntent(geoIds.first().toInt())).addOnCompleteListener {
                if (it.isSuccessful) {
                    result.complete(true)
                } else {
                    result.complete(false)
                }
            }
            result.await()
        } else {
            false
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