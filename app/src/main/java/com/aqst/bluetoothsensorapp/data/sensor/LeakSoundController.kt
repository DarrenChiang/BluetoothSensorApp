package com.aqst.bluetoothsensorapp.data.sensor

import android.content.Context
import android.media.SoundPool
import com.aqst.bluetoothsensorapp.R
import com.aqst.bluetoothsensorapp.domain.sensor.SoundController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LeakSoundController(
    private val context: Context
): SoundController {
    private var soundPool: SoundPool = SoundPool.Builder()
        .setMaxStreams(1)
        .build()

    private var soundId: Int = soundPool.load(context, R.raw.beep, 1)
    private var streamId: Int = 0
    private var isPlaying: Boolean = false

    override fun playSound(rate: Float) {
        streamId = soundPool.play(soundId, 1.0f, 1.0f, 1, -1, rate)
        isPlaying = true
    }

    override fun stopSound() {
        soundPool.stop(streamId)
        isPlaying = false
    }

    override fun playSoundForDuration(rate: Float, seconds: Float) {
        if (isPlaying) {
            return
        }

        playSound(rate)

        GlobalScope.launch(Dispatchers.Main) {
            delay(seconds.toLong() * 1000)
            stopSound()
        }
    }

    override fun release() {
        soundPool.release()
    }
}