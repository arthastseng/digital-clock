package com.ssc.android.vs_digital_clock.presentation.state

import com.ssc.android.vs_digital_clock.data.TimeZoneInfo
import com.ssc.android.vs_digital_clock.network.api.base.SystemError

sealed class TimeDashBoardIntention {
    object Idle : TimeDashBoardIntention()
    object GetPreference : TimeDashBoardIntention()
    object FetchTimeZones : TimeDashBoardIntention()
    object GetRefreshRate : TimeDashBoardIntention()
    object GetLanguage : TimeDashBoardIntention()
    data class RefreshRateChanged(val rate: Int) : TimeDashBoardIntention()
    data class LanguageChanged(val language: String) : TimeDashBoardIntention()
}

sealed class TimeDashBoardAction {
    object Idle : TimeDashBoardAction()
    object NoTimeZoneData : TimeDashBoardAction()
    data class GetPreferenceCompleted(val rate: Int, val language: String): TimeDashBoardAction()
    data class GetRefreshRateCompleted(val data: Int) : TimeDashBoardAction()
    data class FetchTimeZonesCompleted(val data: List<TimeZoneInfo>) : TimeDashBoardAction()
    data class RefreshRateUpdateCompleted(val rate: Int) : TimeDashBoardAction()
    data class GetLanguageCompleted(val language: String) : TimeDashBoardAction()
    data class LanguageUpdateCompleted(val language: String) : TimeDashBoardAction()
    data class ErrorOccur(val error: SystemError) : TimeDashBoardAction()
}

sealed class TimeDashBoardViewState {
    object Idle : TimeDashBoardViewState()
    object NoTimeZoneData : TimeDashBoardViewState()
    data class GetPreferenceCompleted(val rate: Int, val language: String): TimeDashBoardViewState()
    data class GetRefreshRateReady(val data: Int) : TimeDashBoardViewState()
    data class RefreshRateUpdateCompleted(val rate: Int) : TimeDashBoardViewState()
    data class FetchTimeZoneReady(val data: List<TimeZoneInfo>) : TimeDashBoardViewState()
    data class GetLanguageReady(val data: String) : TimeDashBoardViewState()
    data class LanguageUpdateCompleted(val language: String) : TimeDashBoardViewState()
}

sealed class TimeDashBoardEvent {
    data class ErrorOccur(val error: SystemError) : TimeDashBoardEvent()
}