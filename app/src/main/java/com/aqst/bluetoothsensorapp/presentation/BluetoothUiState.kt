package com.aqst.bluetoothsensorapp.presentation

import androidx.compose.ui.graphics.Color
import com.aqst.bluetoothsensorapp.domain.sensor.BluetoothDevice
import com.aqst.bluetoothsensorapp.domain.sensor.DataPoint
import com.aqst.bluetoothsensorapp.domain.sensor.LeakRateConfigState
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import java.util.Timer

data class BluetoothUiState(
    val scannedDevices: List<BluetoothDevice> = emptyList(),
    val pairedDevices: List<BluetoothDevice> = emptyList(),
    val isScanning: Boolean = false,
    val isConnected: Boolean = false,
    val isConnecting: Boolean = false,
    val deviceName: String? = null,
    val errorMessage: String? = null,
    val lastCommand: String? = null,
    val rawData: List<DataPoint> = emptyList(),
    val sgfData: List<DataPoint> = emptyList(),
    val chartData: List<Entry> = emptyList(),
    val chart: LineChart? = null,
    val chartMaxQueue: List<Entry> = emptyList(),
    val chartMinQueue: List<Entry> = emptyList(),
    val pollingInterval: Timer? = null,
    val drawInterval: Timer? = null,

    val isTestDevice: Boolean = false,
    val testData: List<DataPoint> = emptyList(),
    val testDataIndex: Int = 0,

    val isLeakRateConfigScreen: Boolean = false,
    val leakRateConfigState: LeakRateConfigState = LeakRateConfigState(),
    val leakRate: Float = 1e-12f,
    val baseLeakRate: Float = 0f,
    val leakRateColor: Color = Color.Transparent
)
