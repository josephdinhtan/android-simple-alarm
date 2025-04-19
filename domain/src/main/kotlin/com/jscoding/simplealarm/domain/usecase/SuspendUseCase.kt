package com.jscoding.simplealarm.domain.usecase

/**
 * Suspend use cases (asynchronous)
 */
interface SuspendUseCase<in P, out R> {
    suspend operator fun invoke(params: P): R
}