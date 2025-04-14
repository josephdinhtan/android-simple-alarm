package com.jddev.simplealarm.domain.usecase

/**
 * Synchronous use cases (non-suspend)
 */
interface UseCase<in P, out R> {
    operator fun invoke(params: P): R
}