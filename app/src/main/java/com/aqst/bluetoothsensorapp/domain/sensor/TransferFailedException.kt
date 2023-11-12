package com.aqst.bluetoothsensorapp.domain.sensor

import java.io.IOException

class TransferFailedException: IOException("Reading incoming data failed")
