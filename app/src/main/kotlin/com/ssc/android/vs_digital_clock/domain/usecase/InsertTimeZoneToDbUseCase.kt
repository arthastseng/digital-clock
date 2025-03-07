package com.ssc.android.vs_digital_clock.domain.usecase

import com.ssc.android.vs_digital_clock.data.db.TimeZone
import com.ssc.android.vs_digital_clock.domain.repository.DatabaseRepository
import javax.inject.Inject

class InsertTimeZoneToDbUseCase @Inject constructor(
    private val repository: DatabaseRepository
) {
    sealed class Output {
        object Completed: Output()
    }

    suspend operator fun invoke(data: TimeZone): Output {
        repository.insertTimeZone(timeZone = data)
        return Output.Completed
    }
}