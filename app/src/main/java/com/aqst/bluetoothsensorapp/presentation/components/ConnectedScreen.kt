package com.aqst.bluetoothsensorapp.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.dp
import com.aqst.bluetoothsensorapp.presentation.BluetoothUiState

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

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ConnectedScreen(
    state: BluetoothUiState,
    onDisconnect: () -> Unit,
    onStartPolling: () -> Unit,
    onStopPolling: () -> Unit,
    onOpenLimitConfig: () -> Unit,
    onSetLeakDetectionConfig: (Float, Float, Int) -> Unit,
    onResetLeakDetectionConfig: () -> Unit,
    onCloseLimitConfig: () -> Unit,
    onReset: () -> Unit,
    onLoadTestData: () -> Unit
) {
    val isPolling = state.pollingInterval !== null
    val hasLimit = state.limitCoefficient !== null && state.limitExponent !== null
    val limitExpSign = if (state.limitExponent !== null && state.limitExponent >= 0) "+" else ""
    val limitConfigString = "Limit: " +
        state.limitCoefficient.toString() +
        "E" + limitExpSign + state.limitExponent.toString() +
        ", Baseline: " + state.baselineSlope.toString()

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .weight(0.1f)
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Status: Connected to " + (if (state.isTestDevice) "Test Device"  else state.deviceName))
                when {
                    hasLimit -> {
                        Text(text = limitConfigString)
                    }
                }
            }
            IconButton(onClick = onDisconnect) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Disconnect"
                )
            }
        }
        Row(
            modifier = Modifier
                .weight(if (state.isSettingLimit) 0.6f else 0.7f)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "PPM",
                modifier = Modifier.rotateVertically(clockwise = false)
            )
            ChartDisplay(
                chart = state.chart,
                chartModifier = Modifier
                    .fillMaxSize()
            )
        }
        Column(
            modifier = Modifier
                .weight(if (state.isSettingLimit) 0.3f else 0.2f)
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Row(
                modifier = Modifier
                    .weight(if (hasLimit && !state.isSettingLimit) 0.5f else 1f)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                when {
                    state.isSettingLimit -> {
                        LimitInput(
                            modifier = Modifier.fillMaxWidth(),
                            hasLimit = hasLimit,
                            onSetLeakDetectionConfig = onSetLeakDetectionConfig,
                            onResetLeakDetectionConfig = onResetLeakDetectionConfig,
                            onCloseLimitConfig = onCloseLimitConfig
                        )
                    }
                    else -> {
                        when {
                            isPolling -> {
                                Button(onClick = onStopPolling, enabled = !state.isTestDevice) {
                                    Text(text = "Stop Polling")
                                }
                            }
                            else -> {
                                Button(onClick = if (state.isTestDevice) onLoadTestData else onStartPolling) {
                                    Text(text = if (state.isTestDevice) "Load Test Data" else "Start Polling")
                                }
                            }
                        }
                        Button(onClick = onReset) {
                            Text(text = "Reset")
                        }
                        Button(onClick = onOpenLimitConfig) {
                            Text(text = "Set Limit")
                        }
                    }
                }
            }
            if (hasLimit && !state.isSettingLimit) {
                Text(
                    text = if (state.isLeaking) "Leak Detected" else "No Leak",
                    color = if (state.isLeaking) Color.Red else Color.Black
                )
            }
        }
    }
}