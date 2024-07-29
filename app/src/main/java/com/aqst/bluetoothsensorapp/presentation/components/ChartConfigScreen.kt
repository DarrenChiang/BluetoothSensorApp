package com.aqst.bluetoothsensorapp.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

fun isValidWindowSize(value: String): Boolean {
    val size = value.toIntOrNull() ?: return false

    return size in 100..600
}

@Composable
fun ChartConfigScreen(
    modifier: Modifier = Modifier,
    chartWindowSize: Int = 300,
    onSetChartWindowSize: (Int) -> Unit = {},
    onCancel: () -> Unit = {}
) {
    var chartWindowSizeValue by remember { mutableStateOf(chartWindowSize.toString()) }

    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .weight(0.1f)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "Chart Configuration")
        }
        Row(
            modifier = Modifier
                .weight(0.8f)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(
                modifier = Modifier
                    .weight(0.5f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                ConfigRow(
                    label = "Chart Window Size",
                    value = chartWindowSizeValue,
                    hasError = !isValidWindowSize(chartWindowSizeValue),
                    errorMessage = "Must be Int (100 ≤ x ≤ 600)",
                    onValueChange = { chartWindowSizeValue = it }
                )
            }
        }
        Row(
            modifier = Modifier
                .weight(0.1f)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = onCancel) {
                Text(text = "Cancel")
            }
            Button(
                onClick = {
                    onSetChartWindowSize(chartWindowSizeValue.toInt())
                },
                enabled = isValidWindowSize(chartWindowSizeValue)
            ) {
                Text(text = "Save")
            }
        }
    }
}