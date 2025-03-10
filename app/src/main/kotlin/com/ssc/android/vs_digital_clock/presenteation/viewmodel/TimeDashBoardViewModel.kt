package com.ssc.android.vs_digital_clock.presenteation.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.ssc.android.vs_digital_clock.data.TimeZoneInfo
import com.ssc.android.vs_digital_clock.data.db.TimeZone
import com.ssc.android.vs_digital_clock.di.DefaultDispatcher
import com.ssc.android.vs_digital_clock.domain.usecase.FetchTimeZoneUseCase
import com.ssc.android.vs_digital_clock.domain.usecase.FetchTimeZonesFromDbUseCase
import com.ssc.android.vs_digital_clock.network.api.base.SystemError
import com.ssc.android.vs_digital_clock.presenteation.base.MVIViewModel
import com.ssc.android.vs_digital_clock.presenteation.state.TimeDashBoardAction
import com.ssc.android.vs_digital_clock.presenteation.state.TimeDashBoardEvent
import com.ssc.android.vs_digital_clock.presenteation.state.TimeDashBoardIntention
import com.ssc.android.vs_digital_clock.presenteation.state.TimeDashBoardViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

@HiltViewModel
class TimeDashBoardViewModel @Inject constructor(
    private val fetchTimeZonesFromDbUseCase: FetchTimeZonesFromDbUseCase,
    private val fetchTimeZonesUseCase: FetchTimeZoneUseCase,
    @DefaultDispatcher dispatcher: CoroutineDispatcher
) : MVIViewModel<TimeDashBoardIntention, TimeDashBoardAction, TimeDashBoardViewState, TimeDashBoardEvent>(
    defaultDispatcher = dispatcher,
    initialState = TimeDashBoardViewState.Idle
) {
    override suspend fun onIntention(intention: TimeDashBoardIntention) {
        Log.d(TAG, "onIntention: $intention")
        when (intention) {
            is TimeDashBoardIntention.FetchTimeZones -> fetchTimeZones()
            else -> sendAction(TimeDashBoardAction.Idle)
        }
    }

    override suspend fun onReduce(action: TimeDashBoardAction): TimeDashBoardViewState {
        Log.d(TAG, "onReduce: $action")
        return when (action) {
            is TimeDashBoardAction.FetchTimeZonesCompleted ->
                TimeDashBoardViewState.FetchTimeZoneReady(data = action.data)
            else -> TimeDashBoardViewState.Idle
        }
    }

    private fun fetchTimeZones() {
        viewModelScope.launch {
            val output = fetchTimeZonesFromDbUseCase()
            if (output is FetchTimeZonesFromDbUseCase.Output.Result) {
                val timezones = output.data
                val timezoneDeferred = mutableListOf<Deferred<FetchTimeZoneUseCase.Output?>>()
                val timeZoneList = mutableListOf<TimeZoneInfo>()

                timezones.forEach {
                    val getTimezoneTask = async {
                        it.indexKey?.let { timeZoneKeyIndex ->
                            fetchTimeZonesUseCase(data = timeZoneKeyIndex)
                        }
                    }
                    timezoneDeferred.add(getTimezoneTask)
                }

                val timezoneDeferredResults = timezoneDeferred.awaitAll()

                timezoneDeferredResults.forEach { output ->
                    when (output) {
                        is FetchTimeZoneUseCase.Output.Success -> {
                            timeZoneList.add(output.data)
                        }

                        is FetchTimeZoneUseCase.Output.Error -> {
                            sendAction(TimeDashBoardAction.ErrorOccur(error = output.error))
                            coroutineContext.cancel()
                            return@forEach
                        }

                        else -> Unit
                    }
                }
                Log.d(TAG, "fetchTimeZones result : $timeZoneList")
                sendAction(TimeDashBoardAction.FetchTimeZonesCompleted(data = timeZoneList))
            }
        }
    }

    companion object {
        private const val TAG = "TimeDashBoardViewModel"
    }
}