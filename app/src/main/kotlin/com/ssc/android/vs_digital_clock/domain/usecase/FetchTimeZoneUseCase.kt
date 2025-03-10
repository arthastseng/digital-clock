package com.ssc.android.vs_digital_clock.domain.usecase

import com.ssc.android.vs_digital_clock.data.TimeZoneInfo
import com.ssc.android.vs_digital_clock.domain.repository.DashboardRepository
import com.ssc.android.vs_digital_clock.network.api.base.Result
import com.ssc.android.vs_digital_clock.network.api.base.SystemError
import javax.inject.Inject

class FetchTimeZoneUseCase @Inject constructor(
    private val repository: DashboardRepository
) {
    sealed class Output {
        data class Success(val data: TimeZoneInfo) : Output()
        data class Error(val error: SystemError) : Output()
    }

    suspend operator fun invoke(data: String): Output {
        return when (val result = repository.fetchTimeZone(timezone = data)) {
            is Result.Success -> {
                return Output.Success(data = result.data)
            }

            is Result.Error -> {
                return Output.Error(error = result.error)
            }
        }
    }
}