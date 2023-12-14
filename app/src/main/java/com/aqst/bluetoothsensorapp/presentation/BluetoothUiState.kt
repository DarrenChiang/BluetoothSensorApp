package com.aqst.bluetoothsensorapp.presentation

import com.aqst.bluetoothsensorapp.domain.sensor.BluetoothDevice
import com.aqst.bluetoothsensorapp.domain.sensor.DataPoint
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import java.util.Timer

data class BluetoothUiState(
    val scannedDevices: List<BluetoothDevice> = emptyList(),
    val pairedDevices: List<BluetoothDevice> = emptyList(),
    val isScanning: Boolean = false,
    val isConnected: Boolean = false,
    val isConnecting: Boolean = false,
    val errorMessage: String? = null,
    val lastCommand: String? = null,
    val pollingData: List<DataPoint> = emptyList(),
    val chartData: List<Entry> = emptyList(),
    val chart: LineChart? = null,
    val pollingInterval: Timer? = null,
    val drawInterval: Timer? = null,
    val zeroValue: Float? = null
)
