package com.ssc.android.vs_digital_clock.presentation.state

import com.ssc.android.vs_digital_clock.data.db.TimeZone
import com.ssc.android.vs_digital_clock.network.api.base.SystemError

sealed class SettingEditIntention {
    object Idle : SettingEditIntention()
    object FetchTimeZones : SettingEditIntention()
    data class DeleteTimeZones(val data: List<TimeZone>) : SettingEditIntention()
}

sealed class SettingEditAction {
    object Idle : SettingEditAction()
    object NoTimeZoneData : SettingEditAction()
    object DeleteTimeZoneCompleted : SettingEditAction()
    data class FetchTimeZonesReady(val data: List<TimeZone>) : SettingEditAction()
    data class ErrorOccur(val error: SystemError) : SettingEditAction()
}

sealed class SettingEditViewState {
    object Idle : SettingEditViewState()
    object NoTimeZoneData : SettingEditViewState()
    object DeleteTimeZoneCompleted : SettingEditViewState()
    data class GetTimeZoneReady(val data: List<TimeZone>) : SettingEditViewState()
}

sealed class SettingEditEvent {
    data class ErrorOccur(val error: SystemError) : SettingEditEvent()
}