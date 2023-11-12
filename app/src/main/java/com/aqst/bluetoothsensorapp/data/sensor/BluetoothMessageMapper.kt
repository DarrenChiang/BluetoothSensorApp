package com.aqst.bluetoothsensorapp.data.sensor

import com.aqst.bluetoothsensorapp.domain.sensor.BluetoothMessage

fun BluetoothMessage.toByteArray(): ByteArray {
    return message.encodeToByteArray()
}
