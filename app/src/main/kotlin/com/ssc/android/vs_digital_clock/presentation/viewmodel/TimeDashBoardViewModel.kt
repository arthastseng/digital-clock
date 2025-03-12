package com.ssc.android.vs_digital_clock.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.ssc.android.vs_digital_clock.data.TimeZoneInfo
import com.ssc.android.vs_digital_clock.di.DefaultDispatcher
import com.ssc.android.vs_digital_clock.domain.usecase.FetchTimeZoneUseCase
import com.ssc.android.vs_digital_clock.domain.usecase.FetchTimeZonesFromDbUseCase
import com.ssc.android.vs_digital_clock.domain.usecase.GetLanguageUseCase
import com.ssc.android.vs_digital_clock.domain.usecase.GetRefreshRateUseCase
import com.ssc.android.vs_digital_clock.domain.usecase.SetLanguageUseCase
import com.ssc.android.vs_digital_clock.domain.usecase.SetRefreshRateUseCase
import com.ssc.android.vs_digital_clock.network.api.base.SystemError
import com.ssc.android.vs_digital_clock.presentation.base.MVIViewModel
import com.ssc.android.vs_digital_clock.presentation.state.TimeDashBoardAction
import com.ssc.android.vs_digital_clock.presentation.state.TimeDashBoardEvent
import com.ssc.android.vs_digital_clock.presentation.state.TimeDashBoardIntention
import com.ssc.android.vs_digital_clock.presentation.state.TimeDashBoardViewState
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
    private val getLanguageUseCase: GetLanguageUseCase,
    private val setLanguageUseCase: SetLanguageUseCase,
    @DefaultDispatcher dispatcher: CoroutineDispatcher
) : MVIViewModel<TimeDashBoardIntention, TimeDashBoardAction, TimeDashBoardViewState, TimeDashBoardEvent>(
    defaultDispatcher = dispatcher,
    initialState = TimeDashBoardViewState.Idle
) {

    private var job: Job? = null

    override suspend fun onIntention(intention: TimeDashBoardIntention) {
        Log.d(TAG, "onIntention: $intention")
        when (intention) {
            is TimeDashBoardIntention.FetchTimeZones -> fetchTimeZones()
            is TimeDashBoardIntention.GetRefreshRate -> getRefreshRate()
            is TimeDashBoardIntention.GetLanguage -> getLanguage()
            is TimeDashBoardIntention.GetPreference -> getPreference()
            is TimeDashBoardIntention.RefreshRateChanged -> setRefreshRate(rate = intention.rate)
            is TimeDashBoardIntention.LanguageChanged -> setLanguage(language = intention.language)
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

            is TimeDashBoardAction.GetLanguageCompleted ->
                TimeDashBoardViewState.GetLanguageReady(data = action.language)

            is TimeDashBoardAction.LanguageUpdateCompleted ->
                TimeDashBoardViewState.LanguageUpdateCompleted(language = action.language)

            is TimeDashBoardAction.GetPreferenceCompleted ->
                TimeDashBoardViewState.GetPreferenceCompleted(
                    rate = action.rate,
                    language = action.language
                )

            is TimeDashBoardAction.ErrorOccur -> handleErrorOccurAction(action.error)

            else -> TimeDashBoardViewState.Idle
        }
    }

    private fun getLanguage() {
        job = viewModelScope.launch {
            val output = getLanguageUseCase()
            if (output is GetLanguageUseCase.Output.Completed) {
                sendAction(TimeDashBoardAction.GetLanguageCompleted(language = output.data))
            }
        }
    }

    private fun setLanguage(language: String) {
        job = viewModelScope.launch {
            val output = setLanguageUseCase(language = language)
            if (output is SetLanguageUseCase.Output.Completed) {
                sendAction(TimeDashBoardAction.LanguageUpdateCompleted(language = output.data))
            }
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

    private fun getPreference() {
        job = viewModelScope.launch {

            var refreshRate = 1
            var systemLanguage = "en"

            val getRefreshRateTask = async {
                getRefreshRateUseCase()
            }

            val getLanguageTask = async {
                getLanguageUseCase()
            }

            val deferred = listOf(getRefreshRateTask, getLanguageTask).awaitAll()
            deferred.forEach {
                when (it) {
                    is GetRefreshRateUseCase.Output.Completed -> {
                        refreshRate = it.data
                    }

                    is GetLanguageUseCase.Output.Completed -> {
                        systemLanguage = it.data
                    }
                }
            }

            sendAction(
                TimeDashBoardAction.GetPreferenceCompleted(
                    rate = refreshRate,
                    language = systemLanguage
                )
            )
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

    private fun handleErrorOccurAction(error: SystemError): TimeDashBoardViewState {
        viewModelScope.launch {
            sendEvent(TimeDashBoardEvent.ErrorOccur(error))
        }
        return TimeDashBoardViewState.Idle
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }

    companion object {
        private const val TAG = "TimeDashBoardViewModel"
    }
}