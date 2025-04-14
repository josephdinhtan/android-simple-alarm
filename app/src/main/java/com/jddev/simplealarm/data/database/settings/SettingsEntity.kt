package com.jddev.simplealarm.data.database.settings

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings")
data class SettingsEntity(
    @PrimaryKey val id: Long = 1, // Use a fixed ID
    @ColumnInfo(name = "is_24_hour_format") val is24HourFormat: Boolean,
    @ColumnInfo(name = "default_ringtone_uri") val defaultRingtoneUri: String, // URI is stored as a string
    @ColumnInfo(name = "alarm_volume") val alarmVolume: Int,
    @ColumnInfo(name = "is_vibration_enabled") val vibrationEnabled: Boolean,
    @ColumnInfo(name = "snooze_duration") val snoozeDuration: Long, // Duration in milliseconds
    @ColumnInfo(name = "default_label") val defaultLabel: String,
    @ColumnInfo(name = "auto_dismiss_time") val autoDismissTime: Long, // Duration in milliseconds
    @ColumnInfo(name = "is_gradual_volume_enabled") val gradualVolumeEnabled: Boolean,
    @ColumnInfo(name = "theme_mode") val themeMode: Int, // Store theme as index in ThemeMode enum
    @ColumnInfo(name = "is_first_time") val isFirstTime: Boolean
)