package com.aqst.bluetoothsensorapp.domain.sensor

import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import kotlinx.coroutines.flow.StateFlow

interface LineChartController {
    val chart: LineChart
    val dataSets: StateFlow<Map<String, LineChartDataSet>>

    fun configure()

    fun addDataSet(label: String, data: List<Entry>, color: Int)

    fun removeDataSet(label: String)

    fun setData(label: String, data: List<Entry>)

    fun setColor(label: String, color: Int)

    fun setVisibility(label: String, isVisible: Boolean)

    suspend fun drawData()
}