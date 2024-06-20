package com.aqst.bluetoothsensorapp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
fun LeakRateLabel(
    modifier: Modifier = Modifier,
    leakRate: Float = 1e-12f,
    leakRateColor: Color = Color.Transparent
) {
    val min = 1e-11f
    val displayedLeakRate = if (leakRate < min) min.toString() else leakRate.toString()

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Leak Rate:",
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        Box(
            modifier = Modifier
                .clip(shape = RoundedCornerShape(8.dp))
                .background(color = leakRateColor)
                .border(width = 2.dp, color = Color.Black, shape = RoundedCornerShape(8.dp))
        ) {
            Text(
                text = displayedLeakRate,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .sizeIn(minWidth = 200.dp)
            )
        }
    }
}

@Composable
fun ButtonControls(
    modifier: Modifier = Modifier,
    leakRate: Float = 1e-12f,
    leakRateColor: Color = Color.Transparent,
    isPolling: Boolean = false,
    onDisconnect: () -> Unit = {},
    onStartPolling: () -> Unit = {},
    onStopPolling: () -> Unit = {},
    onOpenLeakRateConfiguration: () -> Unit = {}
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
            LeakRateLabel(leakRate = leakRate, leakRateColor = leakRateColor)
        }
        Row(
            modifier = Modifier.weight(0.5f),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = if (isPolling) onStopPolling else onStartPolling) {
                Text(text = if (isPolling) "結束測試" else "開始測試")
            }
            Button(onClick = onOpenLeakRateConfiguration) {
                Text(text = "參數設定")
            }
        }

    }
}