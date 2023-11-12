package com.aqst.bluetoothsensorapp.domain.sensor

data class BluetoothMessage(
    val message: String,
    val senderName: String,
    val isFromLocalUser: Boolean
)
