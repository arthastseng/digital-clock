package com.ssc.android.vs_digital_clock.domain.usecase

import com.ssc.android.vs_digital_clock.data.db.TimeZone
import com.ssc.android.vs_digital_clock.domain.repository.DatabaseRepository
import javax.inject.Inject

class FetchTimeZonesFromDbUseCase @Inject constructor(
    private val repository: DatabaseRepository
) {
    sealed class Output {
        data class Result(val data: List<TimeZone>) : Output()
    }

    suspend operator fun invoke(): Output {
        return Output.Result(data = repository.getAllTimeZones())
    }
}