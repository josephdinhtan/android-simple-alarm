package com.jddev.simplealarm.domain.utils

sealed class Result<out T> {
    data object Loading : Result<Nothing>()
    data class Success<out T>(val data: T): Result<T>()
    data class Error(val exception: Throwable): Result<Nothing>()

    fun <R, T : R> Result<T>.getOrDefault(defaultValue: R): R {
        return if (this is Result.Success) {
            this.data
        } else {
            defaultValue
        }
    }
}