package com.jscoding.simplealarm.domain.entity.exceptions

class AlarmAlreadyExistsException(
    override val message: String = "Alarm already exists",
) : Exception(message)