package com.ssc.android.vs_digital_clock.presenteation.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.ssc.android.vs_digital_clock.data.TimeZoneInfo
import com.ssc.android.vs_digital_clock.di.DefaultDispatcher
import com.ssc.android.vs_digital_clock.domain.usecase.FetchTimeZoneUseCase
import com.ssc.android.vs_digital_clock.domain.usecase.FetchTimeZonesFromDbUseCase
import com.ssc.android.vs_digital_clock.domain.usecase.GetRefreshRateUseCase
import com.ssc.android.vs_digital_clock.domain.usecase.SetRefreshRateUseCase
import com.ssc.android.vs_digital_clock.presenteation.base.MVIViewModel
import com.ssc.android.vs_digital_clock.presenteation.state.TimeDashBoardAction
import com.ssc.android.vs_digital_clock.presenteation.state.TimeDashBoardEvent
import com.ssc.android.vs_digital_clock.presenteation.state.TimeDashBoardIntention
import com.ssc.android.vs_digital_clock.presenteation.state.TimeDashBoardViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

@HiltViewModel
class TimeDashBoardViewModel @Inject constructor(
    private val fetchTimeZonesFromDbUseCase: FetchTimeZonesFromDbUseCase,
    private val fetchTimeZonesUseCase: FetchTimeZoneUseCase,
    private val getRefreshRateUseCase: GetRefreshRateUseCase,
    private val setRefreshRateUseCase: SetRefreshRateUseCase,
    @DefaultDispatcher dispatcher: CoroutineDispatcher
) : MVIViewModel<TimeDashBoardIntention, TimeDashBoardAction, TimeDashBoardViewState, TimeDashBoardEvent>(
    defaultDispatcher = dispatcher,
    initialState = TimeDashBoardViewState.Idle
) {

    private var job : Job? = null

    override suspend fun onIntention(intention: TimeDashBoardIntention) {
        Log.d(TAG, "onIntention: $intention")
        when (intention) {
            is TimeDashBoardIntention.FetchTimeZones -> fetchTimeZones()
            is TimeDashBoardIntention.GetRefreshRate -> getRefreshRate()
            is TimeDashBoardIntention.RefreshRateChanged -> setRefreshRate(rate = intention.rate)
            else -> sendAction(TimeDashBoardAction.Idle)
        }
    }

    override suspend fun onReduce(action: TimeDashBoardAction): TimeDashBoardViewState {
        Log.d(TAG, "onReduce: $action")
        return when (action) {
            is TimeDashBoardAction.FetchTimeZonesCompleted ->
                TimeDashBoardViewState.FetchTimeZoneReady(data = action.data)

            is TimeDashBoardAction.GetRefreshRateCompleted ->
                TimeDashBoardViewState.GetRefreshRateReady(data = action.data)

            is TimeDashBoardAction.RefreshRateUpdateCompleted ->
                TimeDashBoardViewState.RefreshRateUpdateCompleted(rate = action.rate)

            else -> TimeDashBoardViewState.Idle
        }
    }

    private fun getRefreshRate() {
        job = viewModelScope.launch {
            val output = getRefreshRateUseCase()
            if (output is GetRefreshRateUseCase.Output.Completed) {
                sendAction(TimeDashBoardAction.GetRefreshRateCompleted(data = output.data))
            }
        }
    }

    private fun setRefreshRate(rate: Int) {
        job = viewModelScope.launch {
            val output = setRefreshRateUseCase(rate = rate)
            if (output is SetRefreshRateUseCase.Output.Completed) {
                sendAction(TimeDashBoardAction.RefreshRateUpdateCompleted(rate = output.data))
            }
        }
    }

    private fun fetchTimeZones() {
        job = viewModelScope.launch {
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
                            return@launch
                        }

                        else -> Unit
                    }
                }
                Log.d(TAG, "fetchTimeZones result : $timeZoneList")
                sendAction(TimeDashBoardAction.FetchTimeZonesCompleted(data = timeZoneList))
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }

    companion object {
        private const val TAG = "TimeDashBoardViewModel"
    }
}