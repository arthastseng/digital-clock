package com.ssc.android.vs_digital_clock.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.ssc.android.vs_digital_clock.data.db.TimeZone
import com.ssc.android.vs_digital_clock.di.DefaultDispatcher
import com.ssc.android.vs_digital_clock.domain.usecase.FetchAvailableTimeZoneUseCase
import com.ssc.android.vs_digital_clock.domain.usecase.FetchTimeZonesFromDbUseCase
import com.ssc.android.vs_digital_clock.domain.usecase.InsertTimeZoneToDbUseCase
import com.ssc.android.vs_digital_clock.presentation.base.MVIViewModel
import com.ssc.android.vs_digital_clock.presentation.state.SettingAction
import com.ssc.android.vs_digital_clock.presentation.state.SettingEvent
import com.ssc.android.vs_digital_clock.presentation.state.SettingIntention
import com.ssc.android.vs_digital_clock.presentation.state.SettingViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val fetchAvailableTimeZoneUseCase: FetchAvailableTimeZoneUseCase,
    private val fetchTimeZonesFromDbUseCase: FetchTimeZonesFromDbUseCase,
    private val insertTimeZoneToDbUseCase: InsertTimeZoneToDbUseCase,
    @DefaultDispatcher dispatcher: CoroutineDispatcher
) : MVIViewModel<SettingIntention, SettingAction, SettingViewState, SettingEvent>(
    defaultDispatcher = dispatcher,
    initialState = SettingViewState.Idle
) {

    private var timeZones: List<String>? = null

    override suspend fun onIntention(intention: SettingIntention) {
        Log.d(TAG, "onIntention: $intention")
        when (intention) {
            is SettingIntention.PreloadTimeZone -> preloadAvailableTimeZone()
            is SettingIntention.FetchAvailableTimeZone -> getAvailableTimeZone()
            is SettingIntention.FetchTimeZonesFromDB -> fetchTimeZonesFromDatabase()
            is SettingIntention.AddTimeZone -> insertTimeZoneToDB(data = intention.data)
            else -> sendAction(SettingAction.Idle)
        }
    }

    override suspend fun onReduce(action: SettingAction): SettingViewState {
        Log.d(TAG, "onReduce: $action")
        return when (action) {
            is SettingAction.TimeZoneDataReady -> SettingViewState.TimeZoneDataReady(data = action.data)
            is SettingAction.FetchTimeZonesFromDBReady -> SettingViewState.GetTimeZoneFromDbReady(
                data = action.data
            )
            is SettingAction.InsertTimeZoneToDbCompleted -> SettingViewState.InsertTimeZoneToDbCompleted

            else -> SettingViewState.Idle
        }
    }

    private fun fetchTimeZonesFromDatabase() {
        viewModelScope.launch {
            val output = fetchTimeZonesFromDbUseCase()
            if (output is FetchTimeZonesFromDbUseCase.Output.Result) {
                sendAction(SettingAction.FetchTimeZonesFromDBReady(data = output.data))
            }
        }
    }

    private fun preloadAvailableTimeZone() {
        viewModelScope.launch {
            when (val output = fetchAvailableTimeZoneUseCase()) {
                is FetchAvailableTimeZoneUseCase.Output.Success -> {
                    Log.d(TAG, "fetch time zone success: ${output.data}")
                    timeZones = output.data
                }

                is FetchAvailableTimeZoneUseCase.Output.Error -> {
                    Log.e(TAG, "fetch time zone error: ${output.error.errorMsg}")
                    sendAction(SettingAction.ErrorOccur(error = output.error))
                }
            }
        }
    }

    private fun getAvailableTimeZone() {
        Log.d(TAG, "getAvailableTimeZone: ${timeZones?.toString()}")
        viewModelScope.launch {
            timeZones?.let {
                sendAction(SettingAction.TimeZoneDataReady(data = it))
            }

            if (timeZones == null) {
                sendAction(SettingAction.NoTimeZoneData)
            }
        }
    }

    private fun insertTimeZoneToDB(data: TimeZone) {
        viewModelScope.launch {
            insertTimeZoneToDbUseCase(data = data)
            sendAction(SettingAction.InsertTimeZoneToDbCompleted)
        }
    }

    override fun onCleared() {
        timeZones = null
        super.onCleared()
    }

    companion object {
        private const val TAG = "SettingViewModel"
    }
}