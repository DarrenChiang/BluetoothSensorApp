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
                return String.format("%.2e", 10.0.pow(value.toDouble()))
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

    override fun drawData(data: List<Entry>) {
        val lineDataSet = LineDataSet(data, "Sensor Data")
        lineDataSet.color = Color.RED
        lineDataSet.valueTextColor = 2
        lineDataSet.mode = LineDataSet.Mode.LINEAR
        lineDataSet.setDrawValues(true)
        lineDataSet.lineWidth = 2.0f
        val lineData = LineData(lineDataSet)
        chart.data = lineData
        chart.invalidate()
    }
}