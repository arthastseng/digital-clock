package com.ssc.android.vs_digital_clock.service

import com.ssc.android.vs_digital_clock.data.TimeZoneInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

import kotlinx.coroutines.launch
object FloatingWindowUpdateUtil {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val _dataFlow = MutableStateFlow<List<TimeZoneInfo>>(emptyList())  // 用於更新的 StateFlow
    val dataFlow: StateFlow<List<TimeZoneInfo>> get() = _dataFlow
    // 發送資料
    fun updateData(data: List<TimeZoneInfo>) {
        coroutineScope.launch {
            _dataFlow.emit(data)
        }
    }
}