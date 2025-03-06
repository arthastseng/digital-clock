package com.ssc.android.vs_digital_clock.network.api.base

sealed class Result<out Success, out Error> {
    data class Success<ResultType>(val data: ResultType) : Result<ResultType, Nothing>()
    data class Error<Error>(val error: Error) : Result<Nothing, Error>()
}