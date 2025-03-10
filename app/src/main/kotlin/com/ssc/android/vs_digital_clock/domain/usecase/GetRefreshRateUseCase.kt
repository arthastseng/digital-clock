package com.ssc.android.vs_digital_clock.domain.usecase

import com.ssc.android.vs_digital_clock.domain.repository.PreferenceDataRepository
import javax.inject.Inject

class GetRefreshRateUseCase @Inject constructor(
    private val repository: PreferenceDataRepository
) {
    sealed class Output {
        data class Completed(val data: Int) : Output()
    }

    suspend operator fun invoke(): Output {
        val result = repository.getRefreshRate()
        return Output.Completed(data = result)
    }
}