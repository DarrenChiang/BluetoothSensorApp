package com.aqst.bluetoothsensorapp.presentation

import android.content.Context
import android.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aqst.bluetoothsensorapp.domain.sensor.BluetoothController
import com.aqst.bluetoothsensorapp.domain.sensor.BluetoothDeviceDomain
import com.aqst.bluetoothsensorapp.domain.sensor.BluetoothMessage
import com.aqst.bluetoothsensorapp.domain.sensor.ConnectionResult
import com.aqst.bluetoothsensorapp.domain.sensor.DataPoint
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.Timer
import java.util.TimerTask
import javax.inject.Inject
import kotlin.math.log10
import kotlin.math.pow

@HiltViewModel
class BluetoothViewModel @Inject constructor(
    private val bluetoothController: BluetoothController,
    @ApplicationContext private val context: Context
): ViewModel() {
    private val _state = MutableStateFlow(BluetoothUiState())

    val state = combine(
        bluetoothController.scannedDevices,
        bluetoothController.pairedDevices,
        _state
    ) { scannedDevices, pairedDevices, state ->
        state.copy(
            scannedDevices = scannedDevices,
            pairedDevices = pairedDevices
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _state.value)

    private var deviceConnectionJob: Job? = null

    init {
        bluetoothController.isConnected.onEach { isConnected ->
            val chart = LineChart(context)
            setChartConfig(chart)

            val delay = 1000.toLong()
            val timer = Timer()

            val timerTask = object : TimerTask() {
                override fun run() {
                    updateChart()
                }
            }

            // Schedule the TimerTask to run periodically
            timer.schedule(timerTask, 0, delay)

            _state.update {
                it.copy(
                    isConnected = isConnected,
                    chart = chart,
                    drawInterval = timer
                )
            }
        }.launchIn(viewModelScope)

        bluetoothController.errors.onEach { error ->
            _state.update { it.copy(errorMessage = error) }
        }.launchIn(viewModelScope)
    }

    private fun setChartConfig(chart: LineChart) {
        // Customize X-axis
        val xAxis = chart.xAxis
        xAxis?.position = XAxis.XAxisPosition.BOTTOM
//        xAxis?.setLabelCount(data.size, true)

        // Customize Y-axis
        val yAxis = chart.axisLeft

        yAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                if (value.compareTo(0) == 0) return "0"
                return String.format("%.2e", 10.0.pow(value.toDouble()))
            }
        }

        chart.axisRight.isEnabled = false
        chart.description?.isEnabled = false
        chart.legend?.isEnabled = false
        chart.setTouchEnabled(true)
        chart.contentDescription = null
    }

    fun connectToDevice(device: BluetoothDeviceDomain) {
        _state.update {
            if (it.isScanning) bluetoothController.stopDiscovery()

            it.copy(
                isConnecting = true,
                isScanning = false
            )
        }

        deviceConnectionJob = bluetoothController
            .connectToDevice(device)
            .listen()
    }

    fun disconnectFromDevice() {
        deviceConnectionJob?.cancel()
        bluetoothController.closeConnection()
        _state.value.drawInterval?.cancel()
        _state.value.pollingInterval?.cancel()

        _state.update {
            it.copy(
                isConnected = false,
                isConnecting = false,
                drawInterval = null,
                pollingInterval = null
            )
        }
    }

    fun sendMessage(message: String) {
        viewModelScope.launch {
            bluetoothController.trySendMessage(message)
        }
    }

    fun startScan() {
        bluetoothController.startDiscovery()
        _state.update { it.copy(isScanning = true) }
    }

    fun stopScan() {
        bluetoothController.stopDiscovery()
        _state.update { it.copy(isScanning = false) }
    }

    private fun Flow<ConnectionResult>.listen(): Job {
        return onEach { result ->
            when (result) {
                is ConnectionResult.ConnectionEstablished -> {
                    _state.update {
                        it.copy(
                            isConnected = true,
                            isConnecting = false,
                            errorMessage = null
                        )
                    }
                }
                is ConnectionResult.TransferSucceeded -> {
                    handleResponse(result.message)
                }
                is ConnectionResult.Error -> {
                    _state.value.drawInterval?.cancel()
                    _state.value.pollingInterval?.cancel()

                    _state.update {
                        it.copy(
                            isConnected = false,
                            isConnecting = false,
                            errorMessage = result.message,
                            drawInterval = null,
                            pollingInterval = null
                        )
                    }
                }
            }
        }.catch {
            val errorMessage = it.message
            bluetoothController.closeConnection()
            _state.value.drawInterval?.cancel()
            _state.value.pollingInterval?.cancel()

            _state.update {
                it.copy(
                    isConnected = false,
                    isConnecting = false,
                    drawInterval = null,
                    pollingInterval = null,
                    errorMessage = errorMessage
                )
            }
        }.launchIn(viewModelScope)
    }

    override fun onCleared() {
        super.onCleared()
        bluetoothController.release()
    }

    private fun storeCommand(command: String) {
        _state.update {
            it.copy(
                lastCommand = command
            )
        }
    }

    private fun stringToDate(dateString: String, timestampString: String): Date {
        val dateFormat = SimpleDateFormat("dd/MM/yy", Locale.US)
        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.US)

        val date = dateFormat.parse(dateString)
        val time = timeFormat.parse(timestampString)

        val calendar = Calendar.getInstance()
        calendar.time = date

        val timeCalendar = Calendar.getInstance()
        timeCalendar.time = time

        // Extract hours, minutes, and seconds from the timeCalendar
        val hours = timeCalendar.get(Calendar.HOUR_OF_DAY)
        val minutes = timeCalendar.get(Calendar.MINUTE)
        val seconds = timeCalendar.get(Calendar.SECOND)

        // Set hours, minutes, and seconds to the calendar
        calendar.set(Calendar.HOUR_OF_DAY, hours)
        calendar.set(Calendar.MINUTE, minutes)
        calendar.set(Calendar.SECOND, seconds)

        return calendar.time // Returns the combined Date object
    }

    private fun updateChart() {
        val chart = _state.value.chart
        val data = _state.value.chartData

        // Create a LineDataSet from the entries
        val dataSet = LineDataSet(data, "Data Set")

        // Set data and render the chart
        dataSet.color = Color.RED
        dataSet.valueTextColor = 2
        dataSet.mode = LineDataSet.Mode.LINEAR
        dataSet.setDrawValues(true)
        dataSet.lineWidth = 2.0f
        chart?.data = LineData(dataSet)
        chart?.invalidate()
    }

    private fun handleD(message: String) {
        if (message.length < 10) return
        val data: List<String> = message.substring(1).split(',')
        if (data.size < 10) return
        val readingPPM = BigDecimal(data[0])
        val readingMV = BigDecimal(data[1])
        val time: String = data[4]
        val date: String = data[5]
        val range: String = data[7]
        val alarmConditions: String = data[9]

        _state.update {
            val incomingData = DataPoint(readingPPM, readingMV, time, date, range, alarmConditions)

            val pollingData = if (it.pollingData.size >= 120) {
                it.pollingData.drop(1) + incomingData
            } else {
                it.pollingData + incomingData
            }

            val xValue: Float = if (it.chartData.isNotEmpty()) {
                it.chartData.last().x + 1
            } else {
                1.toFloat()
            }

            var yValue: Float = readingPPM.toFloat()

            if (it.zeroValue !== null && it.zeroValue >= yValue) {
                yValue = 0.toFloat()
            }

            if (yValue > 0) {
                yValue = log10(yValue)
            }

            val entry = Entry(xValue, yValue)

            val chartData = if (it.chartData.size >= 120) {
                it.chartData.drop(1) + entry
            } else {
                it.chartData + entry
            }

            it.copy(
                lastCommand = null,
                pollingData = pollingData,
                chartData = chartData
            )
        }
    }

    private fun hexStrToInt(hexStr: String): Int {
        return Integer.decode("0x$hexStr")
    }

    private fun handleG(message: String) {
        if (message.length < 6) return
        val data: List<String> = message.substring(1).split(',')
        if (data.size < 6) return
        val byte1: Int = hexStrToInt(data[0])
        val byte2: Int = hexStrToInt(data[1])
        val byte3: Int = hexStrToInt(data[2])
        val byte4: Int = hexStrToInt(data[3])
        val byte5: Int = hexStrToInt(data[4])
        val byte6: Int = hexStrToInt(data[5])

        val parsedMessage = BluetoothMessage(
            message = "b1: $byte1, b2: $byte2, b3: $byte3, b4: $byte4, b5: $byte5, b6: $byte6",
            senderName = "Device",
            isFromLocalUser = false
        )

        _state.update {
            it.copy(
                lastCommand = null
            )
        }
    }

    private fun handleResponse(btMessage: BluetoothMessage) {
        val message: String = btMessage.message

        // Sometimes sending the "D" command returns a "d" response before the data
        if (message == "d") {
            storeCommand(message)
            return
        }

        if (_state.value.lastCommand == "d") {
            handleD(message)
            return
        }

        if (message[0] == 'd') {
            handleD(message.substring(1))
            return
        }

        if (message[0] == 'g') {
            handleG(message.substring(1))
            return
        }
    }

    fun startPolling() {
        val pollingInterval = _state.value.pollingInterval

        if (pollingInterval == null) {
            val delay = 100.toLong()
            val timer = Timer()

            val timerTask = object : TimerTask() {
                override fun run() {
                    sendMessage("D")
                }
            }

            // Schedule the TimerTask to run periodically
            timer.schedule(timerTask, 0, delay)

            _state.update {
                it.copy(
                    pollingInterval = timer
                )
            }
        }
    }

    fun stopPolling() {
        _state.value.pollingInterval?.cancel()
        _state.update { it.copy(pollingInterval = null) }
    }

    fun activateZero() {
        if (_state.value.zeroValue == null && _state.value.pollingData.isNotEmpty()) {
            _state.update {
                it.copy(
                    zeroValue = it.pollingData.last().ppm.toFloat()
                )
            }
        }
    }

    fun deactivateZero() {
        _state.update {
            it.copy(
                zeroValue = null
            )
        }
    }
}
