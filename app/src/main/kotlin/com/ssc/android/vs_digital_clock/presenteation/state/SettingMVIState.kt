package com.ssc.android.vs_digital_clock.presenteation.state

import com.ssc.android.vs_digital_clock.network.api.base.SystemError

sealed class SettingIntention {
    object Idle : SettingIntention()
    object FetchAvailableTimeZone : SettingIntention()
    object PreloadTimeZone : SettingIntention()
}

sealed class SettingAction {
    object Idle : SettingAction()
    object FetchAvailableTimeZone : SettingAction()
    data class ErrorOccur(val error: SystemError) : SettingAction()
}

sealed class SettingViewState {
    object Idle : SettingViewState()
    object NoTimeZoneData : SettingViewState()
    data class TimeZoneDataReady(val data: List<String>) : SettingViewState()

}

sealed class SettingEvent {
    data class ErrorOccur(val error: SystemError) : SettingEvent()
}

