package com.ssc.android.vs_digital_clock.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.ssc.android.vs_digital_clock.data.db.TimeZone
import com.ssc.android.vs_digital_clock.di.DefaultDispatcher
import com.ssc.android.vs_digital_clock.domain.usecase.DeleteTimeZoneFromDbUseCase
import com.ssc.android.vs_digital_clock.domain.usecase.FetchTimeZonesFromDbUseCase
import com.ssc.android.vs_digital_clock.presentation.base.MVIViewModel
import com.ssc.android.vs_digital_clock.presentation.state.SettingEditAction
import com.ssc.android.vs_digital_clock.presentation.state.SettingEditEvent
import com.ssc.android.vs_digital_clock.presentation.state.SettingEditIntention
import com.ssc.android.vs_digital_clock.presentation.state.SettingEditViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch

@HiltViewModel
class SettingEditModeViewModel @Inject constructor(
    private val fetchTimeZonesFromDbUseCase: FetchTimeZonesFromDbUseCase,
    private val deleteTimeZoneFromDbUseCase: DeleteTimeZoneFromDbUseCase,
    @DefaultDispatcher dispatcher: CoroutineDispatcher
) : MVIViewModel<SettingEditIntention, SettingEditAction, SettingEditViewState, SettingEditEvent>(
    defaultDispatcher = dispatcher,
    initialState = SettingEditViewState.Idle
) {
    override suspend fun onIntention(intention: SettingEditIntention) {
        Log.d(TAG, "onIntention: $intention")
        when (intention) {
            is SettingEditIntention.FetchTimeZones -> fetchTimeZonesFromDB()
            is SettingEditIntention.DeleteTimeZones -> deleteTimeZonesFromDB(data = intention.data)
            else -> sendAction(SettingEditAction.Idle)
        }
    }

    override suspend fun onReduce(action: SettingEditAction): SettingEditViewState {
        Log.d(TAG, "onReduce: $action")
        return when (action) {
            is SettingEditAction.Idle -> SettingEditViewState.Idle

            is SettingEditAction.FetchTimeZonesReady ->
                SettingEditViewState.GetTimeZoneReady(data = action.data)

            is SettingEditAction.DeleteTimeZoneCompleted ->
                SettingEditViewState.DeleteTimeZoneCompleted

            else -> SettingEditViewState.Idle
        }
    }

    private fun fetchTimeZonesFromDB() {
        viewModelScope.launch {
            val output = fetchTimeZonesFromDbUseCase()
            if (output is FetchTimeZonesFromDbUseCase.Output.Result) {
                sendAction(SettingEditAction.FetchTimeZonesReady(data = output.data))
            }
        }
    }

    private fun deleteTimeZonesFromDB(data: List<TimeZone>) {
        viewModelScope.launch {
            val output = deleteTimeZoneFromDbUseCase(data = data)
            if (output is DeleteTimeZoneFromDbUseCase.Output.Completed) {
                sendAction(SettingEditAction.DeleteTimeZoneCompleted)
            }
        }
    }

    companion object {
        private const val TAG = "SettingEditModeViewModel"
    }
}