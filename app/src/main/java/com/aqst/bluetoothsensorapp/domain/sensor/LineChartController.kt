package com.aqst.bluetoothsensorapp.domain.sensor

import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import kotlinx.coroutines.flow.StateFlow

interface LineChartController {
    val chart: LineChart

    fun configure()

    fun setRange(min: Float, max: Float)

    fun drawData(data: List<Entry>)

    fun setLimit(coefficient: Float?, exponent: Int?)
}