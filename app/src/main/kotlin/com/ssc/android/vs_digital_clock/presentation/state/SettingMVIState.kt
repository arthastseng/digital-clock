package com.ssc.android.vs_digital_clock.presentation.state

import com.ssc.android.vs_digital_clock.data.db.TimeZone
import com.ssc.android.vs_digital_clock.network.api.base.SystemError

sealed class SettingIntention {
    object Idle : SettingIntention()
    object FetchTimeZonesFromDB : SettingIntention()
    object FetchAvailableTimeZone : SettingIntention()
    object PreloadTimeZone : SettingIntention()
    data class AddTimeZone(val data: TimeZone) : SettingIntention()
}

sealed class SettingAction {
    object Idle : SettingAction()
    object NoTimeZoneData : SettingAction()
    object InsertTimeZoneToDbCompleted : SettingAction()
    data class FetchTimeZonesFromDBReady(val data: List<TimeZone>) : SettingAction()
    data class TimeZoneDataReady(val data: List<String>) : SettingAction()
    data class ErrorOccur(val error: SystemError) : SettingAction()
}

sealed class SettingViewState {
    object Idle : SettingViewState()
    object NoTimeZoneData : SettingViewState()
    object InsertTimeZoneToDbCompleted : SettingViewState()
    data class TimeZoneDataReady(val data: List<String>) : SettingViewState()
    data class GetTimeZoneFromDbReady(val data: List<TimeZone>) : SettingViewState()
}

sealed class SettingEvent {
    data class ErrorOccur(val error: SystemError) : SettingEvent()
    object FetchAvailableTimeZoneError : SettingEvent()
}

