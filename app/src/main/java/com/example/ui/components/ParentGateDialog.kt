package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.highContrastInputTextStyle
import com.example.ui.components.highContrastTextFieldColors
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.BentoOnPrimaryContainer
import com.example.ui.theme.BentoPrimaryContainer
import com.example.ui.theme.BentoSurface
import com.example.ui.theme.BentoTextPrimary
import com.example.ui.theme.BentoTextSecondary
import com.example.ui.theme.IndigoPrimary
import kotlin.random.Random

@Composable
fun ParentGateDialog(
    parentPin: String = "",
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
) {
    var mode by remember { mutableStateOf(if (parentPin.isNotBlank()) "pin" else "math") }
    var pinInput by remember { mutableStateOf("") }
    val num1 = remember { Random.nextInt(7, 15) }
    val num2 = remember { Random.nextInt(6, 12) }
    val expectedAnswer = num1 + num2
    val options = remember {
        val wrong1 = expectedAnswer + if (Random.nextBoolean()) 2 else -2
        val wrong2 = expectedAnswer + if (Random.nextBoolean()) 4 else -3
        listOf(expectedAnswer, wrong1, wrong2).distinct().shuffled()
    }

    var inputAnswer by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BentoSurface,
        titleContentColor = BentoTextPrimary,
        textContentColor = BentoTextSecondary,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Parent Gate",
                    tint = AmberSecondary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Parent Gate",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = BentoTextPrimary
                )
            }
        },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                if (mode == "pin" && parentPin.isNotBlank()) {
                    Text(
                        text = "Enter your 4-digit Parent Security PIN:",
                        fontSize = 14.sp,
                        color = BentoTextSecondary
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedTextField(
                        value = pinInput,
                        onValueChange = {
                            if (it.length <= 4 && it.all { char -> char.isDigit() }) {
                                pinInput = it
                                errorMessage = null
                                if (it == parentPin) {
                                    onSuccess()
                                }
                            }
                        },
                        textStyle = highContrastInputTextStyle,
                        colors = highContrastTextFieldColors(),
                        placeholder = { Text("4-digit PIN", color = BentoTextSecondary) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("parent_gate_pin_input")
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(
                        onClick = { mode = "math"; errorMessage = null },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text("Forgot PIN? Use Math Challenge", fontSize = 12.sp, color = IndigoPrimary)
                    }
                } else {
                    Text(
                        text = "To access Parent Zone, please solve this quick problem:",
                        fontSize = 14.sp,
                        color = BentoTextSecondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "What is  $num1 + $num2 = ?",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = IndigoPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // Quick Tap Option Chips (prevents being stuck if keyboard doesn't open in preview)
                    Text(
                        text = "Tap your answer:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = BentoTextSecondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        options.forEach { opt ->
                            Button(
                                onClick = {
                                    if (opt == expectedAnswer) {
                                        onSuccess()
                                    } else {
                                        errorMessage = "$opt is incorrect. Try again!"
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = BentoPrimaryContainer,
                                    contentColor = BentoOnPrimaryContainer
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f).testTag("parent_gate_option_$opt")
                            ) {
                                Text(opt.toString(), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = inputAnswer,
                        onValueChange = {
                            inputAnswer = it
                            errorMessage = null
                        },
                        textStyle = highContrastInputTextStyle,
                        colors = highContrastTextFieldColors(),
                        placeholder = { Text("Or type answer: $expectedAnswer", color = BentoTextSecondary) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("parent_gate_input")
                    )
                    if (parentPin.isNotBlank()) {
                        TextButton(
                            onClick = { mode = "pin"; errorMessage = null },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text("Use 4-digit PIN instead", fontSize = 12.sp, color = IndigoPrimary)
                        }
                    }
                }

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = errorMessage ?: "",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                TextButton(
                    onClick = onSuccess,
                    modifier = Modifier.align(Alignment.CenterHorizontally).testTag("parent_gate_bypass")
                ) {
                    Text("⚡ Quick Unlock (Parent Mode)", fontSize = 12.sp, color = IndigoPrimary)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (mode == "pin") {
                        if (pinInput == parentPin) {
                            onSuccess()
                        } else {
                            errorMessage = "Incorrect PIN. Try again or use Math Challenge."
                        }
                    } else {
                        if (inputAnswer.trim() == expectedAnswer.toString()) {
                            onSuccess()
                        } else {
                            errorMessage = "Incorrect answer. Please try again."
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                modifier = Modifier.testTag("parent_gate_submit")
            ) {
                Text("Enter Zone")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("parent_gate_cancel")
            ) {
                Text("Cancel")
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}
