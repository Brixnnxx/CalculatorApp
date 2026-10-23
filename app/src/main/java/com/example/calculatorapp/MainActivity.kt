
package com.example.calculatorapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                CalculatorScreen()
            }
        }
    }
}

@Composable
fun CalculatorScreen() {
    var display by remember { mutableStateOf("0") }
    var firstNumber by remember { mutableStateOf(0.0) }
    var operation by remember { mutableStateOf("") }
    var startNewNumber by remember { mutableStateOf(true) }

    fun pressNumber(number: String) {
        display = if (startNewNumber || display == "0") {
            number
        } else {
            display + number
        }
        startNewNumber = false
    }

    fun pressOperation(symbol: String) {
        firstNumber = display.toDoubleOrNull() ?: 0.0
        operation = symbol
        startNewNumber = true
    }

    fun calculate() {
        val secondNumber = display.toDoubleOrNull() ?: 0.0

        val result = when (operation) {
            "+" -> firstNumber + secondNumber
            "-" -> firstNumber - secondNumber
            "×" -> firstNumber * secondNumber
            "÷" -> {
                if (secondNumber == 0.0) {
                    display = "Cannot divide by zero"
                    operation = ""
                    startNewNumber = true
                    return
                } else {
                    firstNumber / secondNumber
                }
            }
            else -> return
        }

        display = if (result.isFinite()) {
            if (result % 1.0 == 0.0) {
                result.toLong().toString()
            } else {
                result.toString()
            }
        } else {
            "Error"
        }

        operation = ""
        startNewNumber = true
    }

    val rows = listOf(
        listOf("C", "⌫", "+/−", "÷"),
        listOf("7", "8", "9", "×"),
        listOf("4", "5", "6", "-"),
        listOf("1", "2", "3", "+"),
        listOf("0", ".", "=")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = display,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.End,
            maxLines = 2
        )

        rows.forEach { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEach { label ->
                    CalculatorButton(
                        label = label,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        when (label) {
                            "C" -> {
                                display = "0"
                                firstNumber = 0.0
                                operation = ""
                                startNewNumber = true
                            }
                            "⌫" -> {
                                if (!startNewNumber && display.isNotEmpty()) {
                                    display = if (display.length > 1) {
                                        display.dropLast(1)
                                    } else {
                                        "0"
                                    }
                                    if (display == "-") display = "0"
                                }
                            }
                            "+/−" -> {
                                val number = display.toDoubleOrNull()
                                if (number != null && number != 0.0) {
                                    display = if (number > 0) {
                                        "-$display"
                                    } else {
                                        display.removePrefix("-")
                                    }
                                }
                            }
                            "+" , "-", "×", "÷" -> pressOperation(label)
                            "=" -> calculate()
                            "." -> {
                                if (startNewNumber) {
                                    display = "0."
                                    startNewNumber = false
                                } else if (!display.contains(".")) {
                                    display += "."
                                }
                            }
                            else -> pressNumber(label)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CalculatorButton(
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        contentPadding = PaddingValues(2.dp)
    ) {
        Text(
            text = label,
            fontSize = 22.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CalculatorPreview() {
    MaterialTheme {
        CalculatorScreen()
    }
}