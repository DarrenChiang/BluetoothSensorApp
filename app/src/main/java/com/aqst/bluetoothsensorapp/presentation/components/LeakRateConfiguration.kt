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
import androidx.compose.runtime.derivedStateOf
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
import com.aqst.bluetoothsensorapp.domain.sensor.LeakRateConfigState

val floatError = "Must be Float"
val intError = "Must be Int"

fun isValidFloat(value: String): Boolean {
    return value.toFloatOrNull() !== null
}

fun isValidInt(value: String): Boolean {
    return value.toIntOrNull() !== null
}

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
fun LeakRateConfigScreen(
    modifier: Modifier = Modifier,
    config: LeakRateConfigState = LeakRateConfigState(),
    onValidateConfig: (String, String, String, String, String, String) -> LeakRateConfigState? = { _: String, _: String, _: String, _: String, _: String, _: String -> LeakRateConfigState() },
    onSave: (LeakRateConfigState) -> Unit = {},
    onCancel: () -> Unit = {}
) {
    var leakRateStandard by remember { mutableStateOf(config.leakRateStandard.toString()) }
    var pumpingStabilityRate by remember { mutableStateOf(config.pumpingStabilityRate.toString()) }
    var minimumCountForLeakRate by remember { mutableStateOf(config.minimumCountForLeakRate.toString()) }
    var leakRateTestStart by remember { mutableStateOf(config.leakRateTestStart.toString()) }
    var slopeFactor by remember { mutableStateOf(config.slopeFactor.toString()) }
    var leakRateColorSensitivity by remember { mutableStateOf(config.leakRateColorSensitivity.toString()) }

    val finalConfig by remember {
        derivedStateOf {
            onValidateConfig(
                leakRateStandard,
                pumpingStabilityRate,
                minimumCountForLeakRate,
                leakRateTestStart,
                slopeFactor,
                leakRateColorSensitivity
            )
        }
    }

    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .weight(0.1f)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "Leak Rate Configuration")
        }
        Row(
            modifier = Modifier
                .weight(0.8f)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(
                modifier = Modifier
                    .weight(0.5f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                ConfigRow(
                    label = "Leak Rate 標準",
                    value = leakRateStandard,
                    hasError = !isValidFloat(leakRateStandard),
                    errorMessage = floatError,
                    onValueChange = { leakRateStandard = it }
                )
                ConfigRow(
                    label = "Pumping 穩定率",
                    value = pumpingStabilityRate,
                    hasError = !isValidFloat(pumpingStabilityRate),
                    errorMessage = floatError,
                    onValueChange = { pumpingStabilityRate = it }
                )
                ConfigRow(
                    label = "穩定開始次數",
                    value = minimumCountForLeakRate,
                    hasError = !isValidInt(minimumCountForLeakRate),
                    errorMessage = intError,
                    onValueChange = { minimumCountForLeakRate = it }
                )
            }
            Column(
                modifier = Modifier
                    .weight(0.5f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                ConfigRow(
                    label = "LR 測試起始 ppm",
                    value = leakRateTestStart,
                    hasError = !isValidFloat(leakRateTestStart),
                    errorMessage = floatError,
                    onValueChange = { leakRateTestStart = it }
                )
                ConfigRow(
                    label = "變動率 Slope 比例",
                    value = slopeFactor,
                    hasError = !isValidFloat(slopeFactor),
                    errorMessage = floatError,
                    onValueChange = { slopeFactor = it }
                )
                ConfigRow(
                    label = "LR 顏色變動敏感度",
                    value = leakRateColorSensitivity,
                    hasError = !isValidFloat(leakRateColorSensitivity),
                    errorMessage = floatError,
                    onValueChange = { leakRateColorSensitivity = it }
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
                    if (finalConfig !== null) {
                        onSave(finalConfig!!)
                    }
                },
                enabled = finalConfig !== null) {
                Text(text = "Save")
            }
        }
    }
}