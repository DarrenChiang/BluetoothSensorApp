package com.aqst.bluetoothsensorapp.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun DisconnectButton(
    onDisconnect: () -> Unit
) {
    IconButton(onClick = onDisconnect) {
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Disconnect"
        )
    }
}

@Composable
fun ButtonControls(
    modifier: Modifier = Modifier,
    isPolling: Boolean = false,
    onDisconnect: () -> Unit = {},
    onStartPolling: () -> Unit = {},
    onStopPolling: () -> Unit = {},
    onOpenConfigScreen: () -> Unit = {}
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(0.5f),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            DisconnectButton(onDisconnect = onDisconnect)
        }
        Row(
            modifier = Modifier.weight(0.5f),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = if (isPolling) onStopPolling else onStartPolling) {
                Text(text = if (isPolling) "結束測試" else "開始測試")
            }
            Button(onClick = onOpenConfigScreen) {
                Text(text = "參數設定")
            }
        }

    }
}