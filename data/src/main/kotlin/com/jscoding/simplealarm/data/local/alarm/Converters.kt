package com.jscoding.simplealarm.data.local.alarm

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromList(list: List<Int>): String = list.joinToString(",")

    @TypeConverter
    fun toList(data: String): List<Int> =
        if (data.isBlank()) emptyList() else data.split(",").map { it.toInt() }
}