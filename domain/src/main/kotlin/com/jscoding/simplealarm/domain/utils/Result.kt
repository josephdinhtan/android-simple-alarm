package com.jscoding.simplealarm.domain.utils

sealed class Result<out T> {
    data object Loading : com.jscoding.simplealarm.domain.utils.Result<Nothing>()
    data class Success<out T>(val data: T): com.jscoding.simplealarm.domain.utils.Result<T>()
    data class Error(val exception: Throwable): com.jscoding.simplealarm.domain.utils.Result<Nothing>()

    fun <R, T : R> com.jscoding.simplealarm.domain.utils.Result<T>.getOrDefault(defaultValue: R): R {
        return if (this is com.jscoding.simplealarm.domain.utils.Result.Success) {
            this.data
        } else {
            defaultValue
        }
    }
}