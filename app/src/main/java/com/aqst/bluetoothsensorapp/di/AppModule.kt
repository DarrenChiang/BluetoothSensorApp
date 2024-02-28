package com.aqst.bluetoothsensorapp.di

import android.content.Context
import com.aqst.bluetoothsensorapp.data.sensor.AndroidBluetoothController
import com.aqst.bluetoothsensorapp.data.sensor.AndroidLineChartController
import com.aqst.bluetoothsensorapp.data.sensor.FileReader
import com.aqst.bluetoothsensorapp.domain.sensor.BluetoothController
import com.aqst.bluetoothsensorapp.domain.sensor.LineChartController
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideBluetoothController(@ApplicationContext context: Context): BluetoothController {
        return AndroidBluetoothController(context)
    }

    @Provides
    @Singleton
    fun provideLineChartController(@ApplicationContext context: Context): LineChartController {
        return AndroidLineChartController(context)
    }

    @Provides
    @Singleton
    fun provideFileReader(@ApplicationContext context: Context): FileReader {
        return FileReader(context)
    }
}