package com.aqst.bluetoothsensorapp.presentation.components

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.aqst.bluetoothsensorapp.presentation.BluetoothUiState

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    state: BluetoothUiState,
    onDisconnect: () -> Unit,
    onStartPolling: () -> Unit,
    onStopPolling: () -> Unit,
    onActivateZeroClick: () -> Unit,
    onDeactivateZeroClick: () -> Unit
) {
    val isPolling = state.pollingInterval !== null
    val zeroActivated = state.zeroValue !== null

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Status: Connected",
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onDisconnect) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Disconnect"
                )
            }
        }
        Chart(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            chart = state.chart
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            when {
                isPolling -> {
                    Button(onClick = onStopPolling) {
                        Text(text = "Stop Polling")
                    }
                }
                else -> {
                    Button(onClick = onStartPolling) {
                        Text(text = "Start Polling")
                    }
                }
            }
            when {
                zeroActivated -> {
                    Button(onClick = onDeactivateZeroClick) {
                        Text(text = "Stop Zero")
                    }
                }
                else -> {
                    Button(onClick = onActivateZeroClick) {
                        Text(text = "Zero")
                    }
                }
            }

        }
    }
}