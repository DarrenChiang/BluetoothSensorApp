package com.aqst.bluetoothsensorapp.data.sensor

import android.content.Context
import android.graphics.Color
import androidx.compose.runtime.collectAsState
import com.aqst.bluetoothsensorapp.domain.sensor.LineChartController
import com.aqst.bluetoothsensorapp.domain.sensor.LineChartDataSet
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.pow

class AndroidLineChartController(
    context: Context
): LineChartController{
    override val chart = LineChart(context)

    private val _dataSets = MutableStateFlow(mutableMapOf<String, LineChartDataSet>())
    override val dataSets: StateFlow<Map<String, LineChartDataSet>>
        get() = _dataSets.asStateFlow()

    override fun configure() {
        // Customize X-axis
        val xAxis = chart.xAxis
        xAxis?.position = XAxis.XAxisPosition.BOTTOM

        // Customize Y-axis
        val yAxis = chart.axisLeft

        yAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                if (value.compareTo(0) == 0) return "0"
                return String.format("%.2e", 10.0.pow(value.toDouble()))
            }
        }

        chart.axisRight.isEnabled = false
        chart.description?.isEnabled = false
        chart.legend?.isEnabled = false
        chart.setTouchEnabled(true)
        chart.contentDescription = null
    }

    override fun addDataSet(label: String, data: List<Entry>, color: Int) {
        val newData = LineDataSet(data, label)
        newData.color = color
        newData.valueTextColor = 2
        newData.mode = LineDataSet.Mode.LINEAR
        newData.setDrawValues(true)
        newData.lineWidth = 2.0f

        _dataSets.update { dataSets ->
            dataSets[label] = LineChartDataSet(newData, true)
            dataSets
        }
    }

    override fun removeDataSet(label: String) {
        _dataSets.update { dataSets ->
            dataSets.remove(label)
            dataSets
        }
    }

    override fun setData(label: String, data: List<Entry>) {
        _dataSets.update { dataSets ->
            val newData = LineDataSet(data, label)
            val oldData = dataSets[label]?.data
            newData.color = if (oldData !== null) oldData.color else Color.RED
            newData.valueTextColor = 2
            newData.mode = LineDataSet.Mode.LINEAR
            newData.setDrawValues(true)
            newData.lineWidth = 2.0f
            dataSets[label]?.data = newData
            dataSets
        }
    }

    override fun setColor(label: String, color: Int) {
        _dataSets.update { dataSets ->
            dataSets[label]?.data?.color = color
            dataSets
        }
    }

    override fun setVisibility(label: String, isVisible: Boolean) {
        _dataSets.update { dataSets ->
            dataSets[label]?.isVisible = isVisible
            dataSets
        }
    }

    override suspend fun drawData() {
        val data = LineData()

        _dataSets.collect { dataSets ->
            for ((_, dataSet) in dataSets) {
                if (dataSet.isVisible) data.addDataSet(dataSet.data)
            }

            chart.data = data
            chart.invalidate()
        }
    }
}