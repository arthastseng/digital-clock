package com.ssc.android.vs_digital_clock.presenteation.state

import com.ssc.android.vs_digital_clock.data.db.TimeZone
import com.ssc.android.vs_digital_clock.network.api.base.SystemError

sealed class SettingEditIntention {
    object Idle : SettingEditIntention()
    object FetchTimeZonesFromDB : SettingEditIntention()
    data class DeleteTimeZone(val data: TimeZone) : SettingEditIntention()
}

sealed class SettingEditAction {
    object Idle : SettingEditAction()
    object NoTimeZoneData : SettingEditAction()
    object DeleteTimeZoneToDbCompleted : SettingEditAction()
    data class FetchTimeZonesFromDBReady(val data: List<TimeZone>) : SettingEditAction()
    data class ErrorOccur(val error: SystemError) : SettingEditAction()
}

sealed class SettingEditViewState {
    object Idle : SettingEditViewState()
    object NoTimeZoneData : SettingEditViewState()
    object DeleteTimeZoneToDbCompleted : SettingEditViewState()
    data class GetTimeZoneFromDbReady(val data: List<TimeZone>) : SettingEditViewState()
}

sealed class SettingEditEvent {
    data class ErrorOccur(val error: SystemError) : SettingEditEvent()
}