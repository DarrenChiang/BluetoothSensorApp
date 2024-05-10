package com.aqst.bluetoothsensorapp.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

fun isValidFloat(value: String): Boolean {
    return value.toFloatOrNull() !== null
}

fun isValidInt(value: String): Boolean {
    return value.toIntOrNull() !== null
}

fun handleMinus(input: String): String {
    if (input.length > 1) {
        var sub = input.substring(1).replace("-", "")

        if (sub.isEmpty() && input[0] == '-') {
            return "0"
        }

        return input[0] + sub
    } else if (input == "-") {
        return "0"
    }

    return input
}

fun trimForFloat(input: String): String {
    var trimmedInput = input.trim('0') // Initial trim of leading zeroes

    // If the string starts or ends with a dot, we add a leading or trailing zero to prevent loss of decimal point
    if (trimmedInput.startsWith('.')) {
        trimmedInput = "0$trimmedInput"
    }
    if (trimmedInput.endsWith('.')) {
        trimmedInput += '0'
    }

    // Repeat trimming until the condition is no longer met
    while (trimmedInput.length > 1 && (trimmedInput.first() == '0' || trimmedInput.first() == '.' || trimmedInput.last() == '.')) {
        trimmedInput = trimmedInput.trim('0', '.')

        // If the string starts or ends with a dot, we add a leading or trailing zero to prevent loss of decimal point
        if (trimmedInput.startsWith('.')) {
            trimmedInput = "0$trimmedInput"
        }
        if (trimmedInput.endsWith('.')) {
            trimmedInput += '0'
        }
    }

    return trimmedInput
}

fun trimForInteger(input: String): String {
    var trimmedInput = input.trim('0') // Initial trim of leading zeroes

    // Repeat trimming until the condition is no longer met
    while (trimmedInput.length > 1 && trimmedInput.first() == '0') {
        trimmedInput = trimmedInput.trim('0')
    }

    return trimmedInput.ifEmpty { "0" }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LimitInput(
    modifier: Modifier = Modifier,
    hasLimit: Boolean,
    onSetLeakDetectionConfig: (Float, Float, Int) -> Unit,
    onResetLeakDetectionConfig: () -> Unit,
    onCloseLimitConfig: () -> Unit
) {
    var coefficient by remember { mutableStateOf("1.0") }
    var exponent by remember { mutableStateOf("0") }
    var slope by remember { mutableStateOf("0.1") }

    var isValidLimit = isValidFloat(coefficient) && isValidInt(exponent) && isValidFloat(slope)

    val setLeakDetectionConfig = {
        onSetLeakDetectionConfig(
            slope.toFloat(),
            coefficient.toFloat(),
            exponent.toInt()
        )
    }

    Row(modifier = modifier) {
        Column(modifier = Modifier.weight(0.6f)) {
            Row(
                modifier = Modifier.weight(0.5f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Limit:",
                    fontSize = 10.sp
                )
                OutlinedTextField(
                    value = coefficient,
                    onValueChange = {
                        if (isValidFloat(it) || it.length === 0 || it == "-") {
                            coefficient = it
                        }
                    },
                    label = {
                        Text(
                            text = "Coefficient",
                            fontSize = 10.sp
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    visualTransformation = VisualTransformation.None,
                    modifier = Modifier
                        .weight(1f)
                        .onFocusChanged {
                            if (!it.isFocused) {
                                coefficient = handleMinus(coefficient)

                                coefficient = if (coefficient.isNotEmpty() && coefficient[0] == '-') {
                                    "-" + trimForFloat(coefficient.substring(1))
                                } else {
                                    trimForFloat(coefficient)
                                }
                            }
                        }
                )
                OutlinedTextField(
                    value = exponent,
                    onValueChange = {
                        if (isValidInt(it) || it.length === 0 || it == "-") {
                            exponent = it
                        }
                    },
                    label = {
                        Text(
                            text = "Exponent",
                            fontSize = 10.sp
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    visualTransformation = VisualTransformation.None,
                    modifier = Modifier
                        .weight(1f)
                        .onFocusChanged {
                            if (!it.isFocused) {
                                exponent = handleMinus(exponent)

                                exponent = if (exponent.isNotEmpty() && exponent[0] == '-') {
                                    "-" + trimForInteger(exponent.substring(1))
                                } else {
                                    trimForInteger(exponent)
                                }
                            }
                        }
                )
            }
            Row(
                modifier = Modifier.weight(0.5f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Baseline:",
                    fontSize = 10.sp
                )
                OutlinedTextField(
                    value = slope,
                    onValueChange = {
                        if (isValidFloat(it) || it.length === 0 || it == "-") {
                            slope = it
                        }
                    },
                    label = {
                        Text(
                            text = "Slope",
                            fontSize = 10.sp
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    visualTransformation = VisualTransformation.None,
                    modifier = Modifier
                        .weight(1f)
                        .onFocusChanged {
                            if (!it.isFocused) {
                                slope = handleMinus(slope)

                                slope = if (slope.isNotEmpty() && slope[0] == '-') {
                                    "-" + trimForFloat(slope.substring(1))
                                } else {
                                    trimForFloat(slope)
                                }
                            }
                        }
                )
            }
        }

        Column(
            modifier = Modifier.weight(0.4f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                modifier = Modifier
                    .weight(0.1f)
                    .fillMaxWidth(0.9f)
                    .padding(8.dp),
                onClick = onResetLeakDetectionConfig,
                enabled = hasLimit
            ) {
                Text(
                    text = "Delete Config",
                    fontSize = 10.sp
                )
            }
            Button(
                modifier = Modifier
                    .weight(0.1f)
                    .fillMaxWidth(0.9f)
                    .padding(8.dp),
                onClick = setLeakDetectionConfig,
                enabled = isValidLimit
            ) {
                Text(
                    text = "Save Config",
                    fontSize = 10.sp
                )
            }
            Button(
                modifier = Modifier
                    .weight(0.1f)
                    .fillMaxWidth(0.9f)
                    .padding(8.dp),
                onClick = onCloseLimitConfig
            ) {
                Text(
                    text = "Close",
                    fontSize = 10.sp
                )
            }
        }
    }
}