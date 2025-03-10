package com.ssc.android.vs_digital_clock.presenteation.state

import com.ssc.android.vs_digital_clock.data.TimeZoneInfo
import com.ssc.android.vs_digital_clock.network.api.base.SystemError

sealed class TimeDashBoardIntention {
    object Idle : TimeDashBoardIntention()
    object FetchTimeZones : TimeDashBoardIntention()
}

sealed class TimeDashBoardAction {
    object Idle : TimeDashBoardAction()
    object NoTimeZoneData : TimeDashBoardAction()
    data class FetchTimeZonesCompleted(val data: List<TimeZoneInfo>) : TimeDashBoardAction()
    data class ErrorOccur(val error: SystemError) : TimeDashBoardAction()
}

sealed class TimeDashBoardViewState {
    object Idle : TimeDashBoardViewState()
    object NoTimeZoneData : TimeDashBoardViewState()
    data class FetchTimeZoneReady(val data: List<TimeZoneInfo>) : TimeDashBoardViewState()
}

sealed class TimeDashBoardEvent {
    data class ErrorOccur(val error: SystemError) : TimeDashBoardEvent()
}