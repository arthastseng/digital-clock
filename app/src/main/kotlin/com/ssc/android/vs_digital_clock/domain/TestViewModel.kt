package com.ssc.android.vs_digital_clock.domain

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssc.android.vs_digital_clock.network.RetrofitClient
import kotlinx.coroutines.launch

class TestViewModel : ViewModel() {
    fun getAvailableTimeZones() {
        viewModelScope.launch {
            try {
                val timezoneResponse = RetrofitClient.apiService.getAvailableTimeZones()
                timezoneResponse.forEach {
                    Log.d("TestViewModel","timezone: $it")
                }
            } catch (e: Exception) {
                Log.d("TestViewModel","Exception occur: ${e.toString()}")
            }
        }
    }
}