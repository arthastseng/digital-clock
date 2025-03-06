package com.ssc.android.vs_digital_clock.presenteation.state

import com.ssc.android.vs_digital_clock.network.api.base.SystemError

sealed class SettingIntention {
    object Idle : SettingIntention()
    object FetchAvailableTimeZone : SettingIntention()
}

sealed class SettingAction {
    object Idle : SettingAction()

}

sealed class SettingViewState {
    object Idle : SettingViewState()

}

sealed class SettingEvent {
    data class ShowErrorMsg(val error: SystemError) : SettingEvent()
}

