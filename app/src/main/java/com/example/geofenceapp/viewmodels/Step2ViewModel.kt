package com.example.geofenceapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class Step2ViewModel: ViewModel() {

    private val _nextButtonEnabled = MutableLiveData(false)
    val isNextEnabled: LiveData<Boolean>
        get() = _nextButtonEnabled

    private val _internetAvailable = MutableLiveData(true)
    val internetAvailable: LiveData<Boolean>
    get() = _internetAvailable


    fun enableNextButton(enable: Boolean){
        _nextButtonEnabled.value = enable
    }
    fun enableInternet(enable: Boolean){
        _internetAvailable.value = enable
    }
}