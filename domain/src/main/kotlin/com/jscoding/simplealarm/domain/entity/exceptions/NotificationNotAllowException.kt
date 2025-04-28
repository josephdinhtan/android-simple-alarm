package com.jscoding.simplealarm.domain.entity.exceptions

class NotificationNotAllowException(
    override val message: String = "Notification is not allowed",
) : Exception(message)