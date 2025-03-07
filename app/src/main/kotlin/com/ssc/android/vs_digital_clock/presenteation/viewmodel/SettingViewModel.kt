package com.ssc.android.vs_digital_clock.presenteation.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.ssc.android.vs_digital_clock.di.DefaultDispatcher
import com.ssc.android.vs_digital_clock.domain.usecase.FetchAvailableTimeZoneUseCase
import com.ssc.android.vs_digital_clock.presenteation.base.MVIViewModel
import com.ssc.android.vs_digital_clock.presenteation.state.SettingAction
import com.ssc.android.vs_digital_clock.presenteation.state.SettingEvent
import com.ssc.android.vs_digital_clock.presenteation.state.SettingIntention
import com.ssc.android.vs_digital_clock.presenteation.state.SettingViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val fetchAvailableTimeZoneUseCase: FetchAvailableTimeZoneUseCase,
    @DefaultDispatcher dispatcher: CoroutineDispatcher
) : MVIViewModel<SettingIntention, SettingAction, SettingViewState, SettingEvent>(
    defaultDispatcher = dispatcher,
    initialState = SettingViewState.Idle
) {

    private var timeZones: List<String>? = null

    override suspend fun onIntention(intention: SettingIntention) {
        Log.d(TAG, "onIntention: $intention")
        when (intention) {
            is SettingIntention.Idle -> sendAction(SettingAction.Idle)
            is SettingIntention.PreloadTimeZone -> preloadAvailableTimeZone()
            is SettingIntention.FetchAvailableTimeZone -> sendAction(SettingAction.FetchAvailableTimeZone)
        }
    }

    override suspend fun onReduce(action: SettingAction): SettingViewState {
        Log.d(TAG, "onReduce: $action")

        return when (action) {
            is SettingAction.Idle -> return SettingViewState.Idle
            is SettingAction.FetchAvailableTimeZone -> getAvailableTimeZone()
            else -> SettingViewState.Idle
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

    private fun getAvailableTimeZone(): SettingViewState {
        Log.d(TAG, "getAvailableTimeZone: ${timeZones?.toString()}")

        timeZones?.let {
            return SettingViewState.TimeZoneDataReady(data = it)
        }
        return SettingViewState.NoTimeZoneData
    }

    override fun onCleared() {
        timeZones = null
        super.onCleared()
    }

    companion object {
        private const val TAG = "SettingViewModel"
    }
}