package com.jddev.simplealarm.domain.utils

sealed class Result<out T> {
    data object Loading : Result<Nothing>()
    data class Success<out T>(val data: T): Result<T>()
    data class Error(val exception: Throwable): Result<Nothing>()
}