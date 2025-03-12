package com.ssc.android.vs_digital_clock.domain.usecase

import com.ssc.android.vs_digital_clock.domain.repository.PreferenceDataRepository
import javax.inject.Inject

class SetLanguageUseCase @Inject constructor(
    private val repository: PreferenceDataRepository
) {
    sealed class Output {
        data class Completed(val data: String) : Output()
    }

    suspend operator fun invoke(language: String): Output {
        repository.setLanguage(language = language)
        val newLanguage = repository.getLanguage()
        return Output.Completed(data = newLanguage)
    }
}