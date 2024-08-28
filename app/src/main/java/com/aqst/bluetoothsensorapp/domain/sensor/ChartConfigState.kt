package com.aqst.bluetoothsensorapp.domain.sensor

class ChartConfigState(
    val leakThreshold: Float = 1e-3f,
    val beepTime: Float = 2f,
    val chartWindowSize: Int = 300
) {
    operator fun component1() = leakThreshold
    operator fun component2() = beepTime
    operator fun component3() = chartWindowSize

    val leakThresholdLabel = "臨界值"
    val beepTimeLabel = "警報秒數"
    val chartWindowSizeLabel = "Chart Window Size"

    val leakThresholdError = "Must be Float (0.0 ≤ x)"
    val beepTimeError = "Must be Float (0.0 ≤ x ≤ 5.0)"
    val chartWindowSizeError = "Must be Int (100 ≤ x ≤ 600)"

    fun isLeakThresholdStringValid(leakThresholdString: String): Boolean {
        val value = leakThresholdString.toFloatOrNull() ?: return false
        return value >= 0
    }

    fun isBeepTimeStringValid(beepTimeString: String): Boolean {
        val value = beepTimeString.toFloatOrNull() ?: return false
        return value in 0.0..5.0
    }

    fun isChartWindowSizeStringValid(chartWindowSizeString: String): Boolean {
        val value = chartWindowSizeString.toIntOrNull() ?: return false
        return value in 100..600
    }

    fun areConfigStringsValid(
        leakThresholdString: String,
        beepTimeString: String,
        chartWindowSizeString: String
    ): Boolean {
        return isLeakThresholdStringValid(leakThresholdString) &&
            isBeepTimeStringValid(beepTimeString) &&
            isChartWindowSizeStringValid(chartWindowSizeString)
    }
}
