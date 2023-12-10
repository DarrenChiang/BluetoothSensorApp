package com.aqst.bluetoothsensorapp.domain.sensor

import java.math.BigDecimal
import java.util.Date

data class DataPoint(
    val ppm: BigDecimal,
    val mv: BigDecimal,
    val time: String,
    val date: String,
    val range: String,
    val alarmConditions: String
)