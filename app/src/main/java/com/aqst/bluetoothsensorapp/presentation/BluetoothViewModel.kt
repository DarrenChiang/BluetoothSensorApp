package com.aqst.bluetoothsensorapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aqst.bluetoothsensorapp.data.sensor.FileReader
import com.aqst.bluetoothsensorapp.domain.sensor.BluetoothController
import com.aqst.bluetoothsensorapp.domain.sensor.BluetoothDeviceDomain
import com.aqst.bluetoothsensorapp.domain.sensor.BluetoothMessage
import com.aqst.bluetoothsensorapp.domain.sensor.ConnectionResult
import com.aqst.bluetoothsensorapp.domain.sensor.DataPoint
import com.aqst.bluetoothsensorapp.domain.sensor.LineChartController
import com.github.mikephil.charting.data.Entry
import dagger.hilt.android.lifecycle.HiltViewModel
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
import java.util.Timer
import java.util.TimerTask
import javax.inject.Inject
import kotlin.math.log10

@HiltViewModel
class BluetoothViewModel @Inject constructor(
    private val bluetoothController: BluetoothController,
    private val lineChartController: LineChartController,
    private val fileReader: FileReader
): ViewModel() {
    private val _state = MutableStateFlow(BluetoothUiState())

    val state = combine(
        bluetoothController.scannedDevices,
        bluetoothController.pairedDevices,
        bluetoothController.deviceName,
        _state
    ) { scannedDevices, pairedDevices, deviceName, state ->
        state.copy(
            scannedDevices = scannedDevices,
            pairedDevices = pairedDevices,
            deviceName = deviceName
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _state.value)

    private var deviceConnectionJob: Job? = null

    init {
        bluetoothController.isConnected.onEach { isConnected ->
            _state.update {
                it.copy(
                    isConnected = isConnected
                )
            }
        }.launchIn(viewModelScope)

        bluetoothController.deviceName.onEach { deviceName ->
            if (deviceName !== null) {
                lineChartController.configure()
                lineChartController.drawData(emptyList())

                val delay = 1000.toLong()
                val timer = Timer()

                val timerTask = object : TimerTask() {
                    override fun run() {
                        lineChartController.drawData(_state.value.chartData)
                    }
                }

                // Schedule the TimerTask to run periodically
                timer.schedule(timerTask, 0, delay)

                _state.update {
                    it.copy(
                        chart = lineChartController.chart,
                        drawInterval = timer
                    )
                }
            }
        }.launchIn((viewModelScope))

        bluetoothController.errors.onEach { error ->
            _state.update { it.copy(errorMessage = error) }
        }.launchIn(viewModelScope)
    }

    fun connectToDevice(device: BluetoothDeviceDomain) {
        _state.update {
            if (it.isScanning) bluetoothController.stopDiscovery()

            it.copy(
                isConnecting = true,
                isScanning = false,
                isTestDevice = false
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
        _state.value.testDataInterval?.cancel()

        _state.update {
            it.copy(
                isConnected = false,
                isConnecting = false,
                drawInterval = null,
                pollingInterval = null,
                isTestDevice = false,
                testDataInterval = null
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
        }.catch { error ->
            val errorMessage = error.message
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

    private fun handleD(message: String) {
        try {
            val startIndex = message.indexOfFirst { it != 'd' }
            val content = message.substring(startIndex)
            val data: List<String> = content.split(',')

            if (data.size < 6) return

            val readingPPM = BigDecimal(data[0])
            val readingMV = BigDecimal(data[1])
            val time: String = data[4]
            val date: String = data[5]
            val defaultFunc = { _: Int -> "" }
            val range: String = data.getOrElse(6, defaultFunc)
            val alarmConditions: String = data.getOrElse(8, defaultFunc)

            val incomingData = DataPoint(readingPPM, readingMV, time, date, range, alarmConditions)
            addDataPoint(incomingData)
        } catch (error: Throwable) {
            return
        }
    }

    private fun hexStrToInt(hexStr: String): Int {
        return Integer.decode("0x$hexStr")
    }

    private fun handleG(message: String) {
        if (message.length < 6) return
        val content = if (message[0] == 'g') message.substring(1) else message
        val data: List<String> = content.split(',')
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
            handleD(message)
            return
        }

        if (message[0] == 'g') {
            handleG(message)
            return
        }
    }

    fun startPolling() {
        if (_state.value.pollingInterval == null) {
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

    fun reset() {
        stopPolling()

        _state.update {
            it.copy(
                pollingData = emptyList(),
                chartData = emptyList(),
                zeroValue = null
            )
        }
    }

    private fun detectLeak(newData: Entry, data: List<Entry>): Boolean {
        if (newData.y > data.last().y) {
            return true
        }

        return false
    }

    fun acknowledgeLeak() {
        _state.update {
            it.copy(isLeaking = false)
        }
    }

    private fun manageQueue(newValue: Entry, queue: List<Entry>, orderFunction: (Entry, Entry) -> Boolean): List<Entry> {
        if (queue.isEmpty()) {
            return listOf(newValue)
        } else {
            val prevExtremeValue = queue[0]

            return if (orderFunction(newValue, prevExtremeValue)) {
                listOf(newValue)
            } else {
                var tempQueue = queue

                while (orderFunction(newValue, tempQueue.last())) {
                    tempQueue = tempQueue.dropLast(1)
                }

                tempQueue + newValue
            }
        }
    }

    private fun addDataPoint(dataPoint: DataPoint) {
        _state.update {
            val pollingData = if (it.pollingData.size >= 120) {
                it.pollingData.drop(1) + dataPoint
            } else {
                it.pollingData + dataPoint
            }

            val xValue: Float = if (it.chartData.isNotEmpty()) {
                it.chartData.last().x + 1
            } else {
                1.toFloat()
            }

            var yValue: Float = dataPoint.ppm.toFloat()


            if (it.zeroValue !== null && it.zeroValue >= yValue) {
                yValue = 0.toFloat()
            }

            if (yValue > 0) {
                yValue = log10(yValue)
            }

            val entry = Entry(xValue, yValue)

            var isLeaking = it.isLeaking

            if (it.zeroValue !== null && !isLeaking) {
                isLeaking = detectLeak(entry, it.chartData)
            }

            var tempMaxQueue = it.chartMaxQueue
            var tempMinQueue = it.chartMinQueue

            val chartData = if (it.chartData.size >= 120) {
                val droppedData = it.chartData[0]

                if (droppedData.y.equals(tempMaxQueue[0].y)) {
                    tempMaxQueue = tempMaxQueue.drop(1)
                }

                if (droppedData.y.equals(tempMinQueue[0].y)) {
                    tempMinQueue = tempMinQueue.drop(1)
                }

                it.chartData.drop(1) + entry
            } else {
                it.chartData + entry
            }

            var chartMaxQueue = manageQueue(entry, tempMaxQueue) { a, b -> a.y > b.y }
            var chartMinQueue = manageQueue(entry, tempMinQueue) { a, b -> a.y < b.y }

            val rangeMax: Float = chartMaxQueue[0].y * 1.2f
            val rangeMin: Float = chartMinQueue[0].y * 0.8f

            lineChartController.setRange(rangeMin, rangeMax)

            it.copy(
                lastCommand = null,
                pollingData = pollingData,
                chartData = chartData,
                chartMaxQueue = chartMaxQueue,
                chartMinQueue = chartMinQueue,
                isLeaking = isLeaking
            )
        }
    }

    fun addTestDataPoint() {
        if (_state.value.testDataIndex < _state.value.testData.size) {
            addDataPoint(_state.value.testData[_state.value.testDataIndex])
        }

        _state.update {
            val data = it.testData
            val index = it.testDataIndex

            if (index >= data.size) {
                it.testDataInterval?.cancel()

                it.copy(
                    testDataInterval = null,
                    testDataIndex = 0
                )
            } else {
                it.copy(testDataIndex = index + 1)
            }
        }
    }

    private fun getTestField(field: String): String {
        if (field.length > 2) {
            return field.substring(1, field.length - 1)
        }

        return field
    }

    fun loadTestDevice() {
        val content = fileReader.readFile("TestData.txt")
        val lines = content.split("\n")
        val headerCondition: (String) -> Boolean = { it.contains("Time(Seconds)") }
        val dataPoints = mutableListOf<DataPoint>()
        var index = lines.indexOfFirst(headerCondition) + 1

        while (index < lines.size - 1) {
            val line = lines[index]
            val fields = line.split(",").map { getTestField(it) }
            val mv = BigDecimal(fields[1])
            val ppm = BigDecimal(fields[2])
            val time = fields[5]
            val date = fields[6]
            dataPoints.add(DataPoint(ppm, mv, time, date, "", ""))
            index++
        }

        lineChartController.configure()
        lineChartController.drawData(emptyList())

        val delay = 1000.toLong()
        val timer = Timer()

        val timerTask = object : TimerTask() {
            override fun run() {
                lineChartController.drawData(_state.value.chartData)
            }
        }

        // Schedule the TimerTask to run periodically
        timer.schedule(timerTask, 0, delay)

        _state.update {
            if (it.isScanning) bluetoothController.stopDiscovery()

            it.copy(
                isConnected = true,
                isScanning = false,
                isTestDevice = true,
                testData = dataPoints,
                testDataIndex = 0,
                chart = lineChartController.chart,
                drawInterval = timer
            )
        }
    }

    fun loadTestData() {
        if (_state.value.testDataInterval == null) {
            val delay = 500.toLong()
            val timer = Timer()

            val timerTask = object : TimerTask() {
                override fun run() {
                    addTestDataPoint()
                }
            }

            // Schedule the TimerTask to run periodically
            timer.schedule(timerTask, 0, delay)
            _state.update { it.copy(testDataInterval = timer) }
        }
    }
}
