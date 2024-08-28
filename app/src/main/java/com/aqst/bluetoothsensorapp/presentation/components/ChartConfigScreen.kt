package com.aqst.bluetoothsensorapp.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.aqst.bluetoothsensorapp.domain.sensor.ChartConfigState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun ConfigRow(
    modifier: Modifier = Modifier,
    label: String = "Label",
    value: String = "Value",
    hasError: Boolean = false,
    errorMessage: String = "Invalid Value",
    onValueChange: (String) -> Unit = {},
    onFocusChange: () -> Unit = {},
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            modifier = Modifier
                .weight(0.5f)
                .padding(4.dp),
            textAlign = TextAlign.Center
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            isError = hasError,
            label = {
                if (hasError) {
                    Text(text = errorMessage)
                }
            },
            modifier = Modifier
                .weight(0.5f)
                .padding(4.dp)
                .onFocusChanged {
                    if (!it.isFocused) {
                        onFocusChange()
                    }
                },
            visualTransformation = VisualTransformation.None,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                }
            )
        )
    }
}

@Composable
fun ChartConfigScreen(
    modifier: Modifier = Modifier,
    configState: ChartConfigState = ChartConfigState(),
    onSaveAndClose: (ChartConfigState) -> Unit = { _ -> },
    onCancel: () -> Unit = {}
) {
    val (
        leakThreshold,
        beepTime,
        chartWindowSize
    ) = configState

    var leakThresholdString by remember { mutableStateOf(leakThreshold.toString()) }
    var beepTimeString by remember { mutableStateOf(beepTime.toString()) }
    var chartWindowSizeString by remember { mutableStateOf(chartWindowSize.toString()) }

    val isConfigValid = configState.areConfigStringsValid(leakThresholdString, beepTimeString, chartWindowSizeString)

    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .weight(0.1f)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "Chart Configuration")
        }
        Row(
            modifier = Modifier
                .weight(0.8f)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                ConfigRow(
                    label = configState.leakThresholdLabel,
                    value = leakThresholdString,
                    hasError = !configState.isLeakThresholdStringValid(leakThresholdString),
                    errorMessage = configState.leakThresholdError,
                    onValueChange = { leakThresholdString = it }
                )
                ConfigRow(
                    label = configState.beepTimeLabel,
                    value = beepTimeString,
                    hasError = !configState.isBeepTimeStringValid(beepTimeString),
                    errorMessage = configState.beepTimeError,
                    onValueChange = { beepTimeString = it }
                )
                ConfigRow(
                    label = configState.chartWindowSizeLabel,
                    value = chartWindowSizeString,
                    hasError = !configState.isChartWindowSizeStringValid(chartWindowSizeString),
                    errorMessage = configState.chartWindowSizeError,
                    onValueChange = { chartWindowSizeString = it }
                )
            }

        }
        Row(
            modifier = Modifier
                .weight(0.1f)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = onCancel) {
                Text(text = "Cancel")
            }
            Button(
                onClick = {
                    if (isConfigValid) {
                        onSaveAndClose(ChartConfigState(
                            leakThresholdString.toFloat(),
                            beepTimeString.toFloat(),
                            chartWindowSizeString.toInt()
                        ))
                    }
                },
                enabled = isConfigValid
            ) {
                Text(text = "Save")
            }
        }
    }
}