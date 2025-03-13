package com.ssc.android.vs_digital_clock.service

import com.ssc.android.vs_digital_clock.data.TimeZoneInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

import kotlinx.coroutines.launch
object FloatingWindowUpdateUtil {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val _dataFlow = MutableSharedFlow<List<TimeZoneInfo>>()  // 用於更新的 StateFlow
    val dataFlow: SharedFlow<List<TimeZoneInfo>> get() = _dataFlow
    // 發送資料
    fun updateData(data: List<TimeZoneInfo>) {
        coroutineScope.launch {
            _dataFlow.emit(data)
        }
    }
}