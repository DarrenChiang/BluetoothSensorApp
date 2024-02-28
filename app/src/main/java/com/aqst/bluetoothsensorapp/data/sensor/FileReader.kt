package com.aqst.bluetoothsensorapp.data.sensor

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader

class FileReader(private val context: Context) {
    fun readFile(fileName: String): String {
        val stringBuilder = StringBuilder()
        try {
            val inputStream = context.assets.open(fileName)
            val reader = BufferedReader(InputStreamReader(inputStream))
            var line: String? = reader.readLine()

            while (line != null) {
                stringBuilder.append(line).append("\n")
                line = reader.readLine()
            }

            reader.close()
            inputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return stringBuilder.toString()
    }
}