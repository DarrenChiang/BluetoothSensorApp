package com.aqst.bluetoothsensorapp.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aqst.bluetoothsensorapp.domain.sensor.LeakRateConfigState
import com.aqst.bluetoothsensorapp.presentation.BluetoothUiState
@Composable
fun ConnectedScreen(
    state: BluetoothUiState,
    onDisconnect: () -> Unit,
    onStartPolling: () -> Unit,
    onStopPolling: () -> Unit,
    onLoadTestData: () -> Unit,
    onValidateLeakRateConfiguration: (String, String, String, String, String, String) -> LeakRateConfigState?,
    onOpenLeakRateConfiguration: () -> Unit,
    onCancelLeakRateConfiguration: () -> Unit,
    onSaveLeakRateConfiguration: (LeakRateConfigState) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        if (state.isLeakRateConfigScreen) {
            LeakRateConfigScreen(
                config = state.leakRateConfigState,
                onValidateConfig = onValidateLeakRateConfiguration,
                onSave = onSaveLeakRateConfiguration,
                onCancel = onCancelLeakRateConfiguration,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            ButtonControls(
                leakRate = state.leakRate,
                leakRateColor = state.leakRateColor,
                isPolling = state.pollingInterval !== null,
                onDisconnect = onDisconnect,
                onStartPolling = if (state.isTestDevice) onLoadTestData else onStartPolling,
                onStopPolling = onStopPolling,
                onOpenLeakRateConfiguration = onOpenLeakRateConfiguration,
                modifier = Modifier
                    .weight(0.2f)
                    .fillMaxWidth()
            )
            ChartDisplay(
                label = "Leak Rate",
                chart = state.chart,
                modifier = Modifier
                    .weight(0.8f)
                    .fillMaxWidth()
            )
        }
    }
}