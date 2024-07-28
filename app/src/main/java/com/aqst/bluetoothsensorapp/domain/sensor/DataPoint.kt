package com.aqst.bluetoothsensorapp.domain.sensor

import java.math.BigDecimal
import java.util.Date

data class DataPoint(
    val ppm: Float,
    val mv: Float,
    val time: Float,
    val date: String,
    val range: String,
    val alarmConditions: String
)