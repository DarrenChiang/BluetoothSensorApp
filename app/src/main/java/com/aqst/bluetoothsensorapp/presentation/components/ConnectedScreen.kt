package com.aqst.bluetoothsensorapp.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.unit.dp
import com.aqst.bluetoothsensorapp.presentation.BluetoothUiState

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ConnectedScreen(
    state: BluetoothUiState,
    onDisconnect: () -> Unit,
    onStartPolling: () -> Unit,
    onStopPolling: () -> Unit,
    onActivateZeroClick: () -> Unit,
    onDeactivateZeroClick: () -> Unit,
    onReset: () -> Unit
) {
    val isPolling = state.pollingInterval !== null
    val zeroActivated = state.zeroValue !== null

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Status: Connected to " + state.deviceName,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onDisconnect) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Disconnect"
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .rotate(270f)
            ) {
                Text(text = "Leak Rate (PPM)")
            }
            ChartDisplay(
                chart = state.chart,
                chartModifier = Modifier
                    .fillMaxSize()
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            when {
                isPolling -> {
                    Button(onClick = onStopPolling) {
                        Text(text = "Stop Polling")
                    }
                }
                else -> {
                    Button(onClick = onStartPolling) {
                        Text(text = "Start Polling")
                    }
                }
            }
            when {
                zeroActivated -> {
                    Button(onClick = onDeactivateZeroClick) {
                        Text(text = "Stop Zero")
                    }
                }
                else -> {
                    Button(onClick = onActivateZeroClick) {
                        Text(text = "Zero")
                    }
                }
            }
            Button(onClick = onReset) {
                Text(text = "Reset")
            }
        }
    }
}