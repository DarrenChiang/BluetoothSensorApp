package com.aqst.bluetoothsensorapp.presentation

import com.aqst.bluetoothsensorapp.domain.sensor.BluetoothDevice
import com.aqst.bluetoothsensorapp.domain.sensor.BluetoothMessage
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import java.util.Timer

data class BluetoothUiState(
    val scannedDevices: List<BluetoothDevice> = emptyList(),
    val pairedDevices: List<BluetoothDevice> = emptyList(),
    val isConnected: Boolean = false,
    val isConnecting: Boolean = false,
    val errorMessage: String? = null,
    val messages: List<BluetoothMessage> = emptyList(),
    val lastCommand: String? = null,
    val showChart: Boolean = false,
    val data: List<Entry> = emptyList(),
    val chart: LineChart? = null,
    val pollingInterval: Timer? = null,
    val drawInterval: Timer? = null
)
