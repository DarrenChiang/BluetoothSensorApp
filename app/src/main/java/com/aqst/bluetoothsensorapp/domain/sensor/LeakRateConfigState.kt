package com.aqst.bluetoothsensorapp.domain.sensor

data class LeakRateConfigState(
    val leakRateStandard: Float = 1e-12f,
    val pumpingStabilityRate: Float = 0.1f,
    val minimumCountForLeakRate: Int = 5,
    val leakRateTestStart: Float = 100f,
    val slopeFactor: Float = 100f,
    val leakRateColorSensitivity: Float = 5f
)
