package com.aqst.bluetoothsensorapp.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart

fun Modifier.rotateVertically(clockwise: Boolean = true): Modifier {
    val rotate = rotate(if (clockwise) 90f else -90f)

    val adjustBounds = layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)

        layout(placeable.height, placeable.width) {
            placeable.place(
                x = -(placeable.width / 2 - placeable.height / 2),
                y = -(placeable.height / 2 - placeable.width / 2)
            )
        }
    }
    return rotate then adjustBounds
}

@Composable
fun ChartDisplay(
    modifier: Modifier = Modifier,
    yLabel: String = "",
    xLabel: String = "",
    chart: LineChart?
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(0.9f)
        ) {
            Text(
                text = yLabel,
                fontSize = 12.sp,
                modifier = Modifier.rotateVertically(clockwise = false)
            )
            if (chart != null) {
                AndroidView(
                    factory = { chart },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        Row(modifier = Modifier.weight(0.1f)) {
            Text(
                text = xLabel,
                fontSize = 12.sp
            )
        }
    }
}