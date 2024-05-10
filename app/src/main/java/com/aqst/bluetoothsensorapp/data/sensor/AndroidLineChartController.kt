package com.aqst.bluetoothsensorapp.data.sensor

import android.content.Context
import android.graphics.Color
import com.aqst.bluetoothsensorapp.domain.sensor.LineChartController
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlin.math.pow

class AndroidLineChartController(
    context: Context
): LineChartController{
    override val chart = LineChart(context)

    private var _data: List<Entry> = emptyList()
    private var _limit: Float? = null

    override fun configure() {
        // Customize X-axis
        val xAxis = chart.xAxis
        xAxis?.position = XAxis.XAxisPosition.BOTTOM

        // Customize Y-axis
        val yAxis = chart.axisLeft
        yAxis.axisMinimum = 0f

        yAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                if (value.compareTo(0) == 0) return "0"
                return String.format("%.2e", value)
            }
        }

        chart.axisRight.isEnabled = false
        chart.description?.isEnabled = false
        chart.legend?.isEnabled = false
        chart.contentDescription = null
        chart.setTouchEnabled(false)
        chart.setPinchZoom(false)
        chart.isDragEnabled = false
    }

    override fun setRange(min: Float, max: Float) {
        // Customize Y-axis
        val yAxis = chart.axisLeft
        yAxis.axisMinimum = min
        yAxis.axisMaximum = max
        chart.setTouchEnabled(false)
        chart.setPinchZoom(false)
        chart.isDragEnabled = false
        chart.invalidate()
    }

    private fun draw() {
        val lineDataSet = LineDataSet(_data, "Sensor Data")
        lineDataSet.color = Color.BLUE
        lineDataSet.valueTextColor = 2
        lineDataSet.mode = LineDataSet.Mode.LINEAR
        lineDataSet.setDrawValues(true)
        lineDataSet.lineWidth = 2.0f
        val lineData = LineData(lineDataSet)


        if (_limit !== null && _data.size > 1) {
            val entries = mutableListOf<Entry>()
            entries.add(Entry(_data.first().x, _limit!!))
            entries.add(Entry(_data.last().x, _limit!!))
            val limitSet = LineDataSet(entries, "Horizontal Line")
            limitSet.color = Color.RED
            limitSet.valueTextColor = 2
            limitSet.mode = LineDataSet.Mode.LINEAR
            limitSet.setDrawValues(true)
            limitSet.lineWidth = 2.0f
            lineData.addDataSet(limitSet)
        }

        chart.data = lineData
        chart.invalidate()
    }

    override fun drawData(data: List<Entry>) {
        _data = data
        draw()
    }

    override fun setLimit(coefficient: Float?, exponent: Int?) {
        _limit = if (coefficient !== null && exponent !== null) {
            coefficient * 10.0.pow(exponent.toDouble()).toFloat()
        } else {
            null
        }

        draw()
    }
}