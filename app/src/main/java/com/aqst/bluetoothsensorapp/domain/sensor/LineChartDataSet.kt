package com.aqst.bluetoothsensorapp.domain.sensor

import com.github.mikephil.charting.data.LineDataSet

data class LineChartDataSet(
    var data: LineDataSet,
    var isVisible: Boolean
)
