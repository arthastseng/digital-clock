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
    override suspend fun onIntention(intention: SettingIntention) {
        when(intention) {
            is SettingIntention.Idle -> Unit
            is SettingIntention.FetchAvailableTimeZone -> fetchAvailableTimeZone()
        }
    }

    override suspend fun onReduce(action: SettingAction): SettingViewState {
        TODO("Not yet implemented")
    }

    private fun fetchAvailableTimeZone() {
        viewModelScope.launch {
            when(val output = fetchAvailableTimeZoneUseCase()) {
                is FetchAvailableTimeZoneUseCase.Output.Success -> {
                    Log.d(TAG,"fetch time zone success: ${output.data}")
                }

                is FetchAvailableTimeZoneUseCase.Output.Error -> {}

            }
        }
    }

    companion object {
        private const val TAG = "SettingViewModel"
    }
}