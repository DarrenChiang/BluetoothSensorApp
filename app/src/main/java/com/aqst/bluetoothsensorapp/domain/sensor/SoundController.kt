package com.aqst.bluetoothsensorapp.domain.sensor

interface SoundController {
    fun playSound(rate: Float = 1f)

    fun stopSound()

    fun playSoundForDuration(rate: Float = 1f, seconds: Float = 2f)

    fun release()
}