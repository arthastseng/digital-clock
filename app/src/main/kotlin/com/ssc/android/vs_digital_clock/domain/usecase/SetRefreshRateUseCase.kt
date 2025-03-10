package com.ssc.android.vs_digital_clock.domain.usecase

import com.ssc.android.vs_digital_clock.domain.repository.PreferenceDataRepository
import javax.inject.Inject

class SetRefreshRateUseCase @Inject constructor(
    private val repository: PreferenceDataRepository
) {
    sealed class Output {
        data class Completed(val data: Int) : Output()
    }

    suspend operator fun invoke(rate: Int): Output {
        repository.setRefreshRate(rate = rate)
        val newRate = repository.getRefreshRate()
        return Output.Completed(data = newRate)
    }
}