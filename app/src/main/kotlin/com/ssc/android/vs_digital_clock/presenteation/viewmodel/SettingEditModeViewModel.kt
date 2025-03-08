package com.ssc.android.vs_digital_clock.presenteation.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.ssc.android.vs_digital_clock.di.DefaultDispatcher
import com.ssc.android.vs_digital_clock.domain.usecase.FetchTimeZonesFromDbUseCase
import com.ssc.android.vs_digital_clock.presenteation.base.MVIViewModel
import com.ssc.android.vs_digital_clock.presenteation.state.SettingEditAction
import com.ssc.android.vs_digital_clock.presenteation.state.SettingEditEvent
import com.ssc.android.vs_digital_clock.presenteation.state.SettingEditIntention
import com.ssc.android.vs_digital_clock.presenteation.state.SettingEditViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch

@HiltViewModel
class SettingEditModeViewModel @Inject constructor(
    private val fetchTimeZonesFromDbUseCase: FetchTimeZonesFromDbUseCase,
    @DefaultDispatcher dispatcher: CoroutineDispatcher
) : MVIViewModel<SettingEditIntention, SettingEditAction, SettingEditViewState, SettingEditEvent>(
    defaultDispatcher = dispatcher,
    initialState = SettingEditViewState.Idle
) {
    override suspend fun onIntention(intention: SettingEditIntention) {
        Log.d(TAG, "onIntention: $intention")
        when (intention) {
            is SettingEditIntention -> fetchTimeZonesFromDatabase()
        }
    }

    override suspend fun onReduce(action: SettingEditAction): SettingEditViewState {
        Log.d(TAG, "onReduce: $action")
        return when (action) {
            is SettingEditAction.Idle -> SettingEditViewState.Idle
            is SettingEditAction.FetchTimeZonesFromDBReady ->
                SettingEditViewState.GetTimeZoneFromDbReady(data = action.data)

            else -> SettingEditViewState.Idle
        }
    }

    private fun fetchTimeZonesFromDatabase() {
        viewModelScope.launch {
            val output = fetchTimeZonesFromDbUseCase()
            if (output is FetchTimeZonesFromDbUseCase.Output.Result) {
                sendAction(SettingEditAction.FetchTimeZonesFromDBReady(data = output.data))
            }
        }
    }

    companion object {
        private const val TAG = "SettingEditModeViewModel"
    }
}