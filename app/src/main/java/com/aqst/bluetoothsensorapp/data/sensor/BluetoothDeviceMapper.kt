package com.aqst.bluetoothsensorapp.data.sensor

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import com.aqst.bluetoothsensorapp.domain.sensor.BluetoothDeviceDomain

@SuppressLint("MissingPermission")
fun BluetoothDevice.toBluetoothDeviceDomain(): BluetoothDeviceDomain {
    return BluetoothDeviceDomain(
        name = name,
        address = address
    )
}
