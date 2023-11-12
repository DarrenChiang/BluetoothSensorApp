package com.aqst.bluetoothsensorapp.data.sensor

import android.bluetooth.BluetoothSocket
import com.aqst.bluetoothsensorapp.domain.sensor.BluetoothMessage
import com.aqst.bluetoothsensorapp.domain.sensor.TransferFailedException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.IOException

class BluetoothDataTransferService(
    private val socket: BluetoothSocket
) {
    fun listenForIncomingMessages(): Flow<BluetoothMessage> {
        return flow {
            if (!socket.isConnected) {
                return@flow
            }

            val buffer = ByteArray(1024)

            while (true) {
                val byteCount = try {
                    socket.inputStream.read(buffer)
                } catch (e: IOException) {
                    throw TransferFailedException()
                }

                emit(
                    BluetoothMessage(
                        message = buffer.decodeToString(endIndex = byteCount),
                        senderName = "Sensor",
                        isFromLocalUser = false
                    )
                )
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun sendMessage(bytes: ByteArray): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                socket.outputStream.write(bytes)
            } catch(e: IOException) {
                return@withContext false
            }

            true
        }
    }
}
