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
    onSendMessage: (String) -> Unit,
    onStartPolling: () -> Unit,
    onToggleChart: () -> Unit
) {
    val message = rememberSaveable {
        mutableStateOf("")
    }

    val keyboardController = LocalSoftwareKeyboardController.current

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
        when {
            state.showChart -> {
                Chart(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    chart = state.chart
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(state.messages) { message ->
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            ChatMessage(
                                message = message,
                                modifier = Modifier
                                    .align(
                                        if (message.isFromLocalUser) Alignment.End else Alignment.Start
                                    )
                            )
                        }
                    }
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = message.value,
                onValueChange = { message.value = it },
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(text = "Message")
                }
            )
            IconButton(onClick = {
                onSendMessage(message.value)
                message.value = ""
                keyboardController?.hide()
            }) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send message"
                )
            }
            Button(onClick = onStartPolling) {
                Text(text = "Start Polling")
            }
            Button(onClick = onToggleChart) {
                when {
                    state.showChart -> {
                        Text(text = "Hide Chart")
                    }
                    else -> {
                        Text(text = "Show Chart")
                    }
                }
            }
        }
    }
}