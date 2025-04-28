package com.jscoding.simplealarm.data.local

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

internal object SettingsPreferencesKeys {
    val useDynamicColors = booleanPreferencesKey(name = "use_dynamic_colors")
    val is24HourFormat = booleanPreferencesKey("is_24_hour_format")
    val defaultRingtoneTitle = stringPreferencesKey("default_ringtone_title")
    val defaultRingtoneUri = stringPreferencesKey("default_ringtone_uri")
    val defaultPreAlarmNotificationMin = intPreferencesKey("default_pre_alarm_notification_min")
    val isVibrationEnabled = booleanPreferencesKey("vibration_enabled")
    val snoozeDuration = intPreferencesKey("snooze_duration") // store in minutes or seconds
    val defaultLabel = stringPreferencesKey("default_label")
    val autoDismissTime = intPreferencesKey("auto_dismiss_time") // in seconds
    val volumeFadeDuration = intPreferencesKey("volume_fade_duration") // in seconds
    val themeStyle = stringPreferencesKey("theme_style")
    val isFirstTime = booleanPreferencesKey("is_first_time")
}