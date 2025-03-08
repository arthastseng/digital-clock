package com.ssc.android.vs_digital_clock.domain.usecase

import com.ssc.android.vs_digital_clock.domain.repository.SettingRepository
import com.ssc.android.vs_digital_clock.network.api.base.Result
import com.ssc.android.vs_digital_clock.network.api.base.SystemError
import javax.inject.Inject

class FetchAvailableTimeZoneUseCase @Inject constructor(
    private val repository: SettingRepository
) {
    sealed class Output {
        data class Success(val data: List<String>) : Output()
        data class Error(val error: SystemError) : Output()
    }

    suspend operator fun invoke(): Output {
        return when (val result = repository.fetchAvailableTimeZones()) {
            is Result.Success -> {
                return Output.Success(data = result.data)
            }

            is Result.Error -> {
                return Output.Error(error = result.error)
            }
        }
    }
}