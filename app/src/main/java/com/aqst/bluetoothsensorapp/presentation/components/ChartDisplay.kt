package com.aqst.bluetoothsensorapp.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart

@Composable
fun ChartDisplay(
    chart: LineChart?,
    chartModifier: Modifier
) {
    if (chart != null) {
        AndroidView(
            modifier = chartModifier,
            factory = { chart }
        )
    }
}