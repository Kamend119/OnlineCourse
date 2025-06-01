package com.example.onlinecourse.function

import android.widget.NumberPicker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun TimePickerWithHMS(
    timePasses: String,
    onTimeSelected: (String) -> Unit
) {
    val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
    var selectedTime by remember {
        mutableStateOf(
            try {
                LocalTime.parse(timePasses, formatter)
            } catch (e: Exception) {
                LocalTime.of(0, 0, 0)
            }
        )
    }

    var showDialog by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = selectedTime.format(formatter),
        onValueChange = {},
        readOnly = true,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showDialog = true },
        label = { Text("Время выполнения") },
        trailingIcon = {
            Icon(Icons.Default.AccessTime, contentDescription = "Выбрать время")
        },
        colors = OutlinedTextFieldDefaults.colors(
            disabledTextColor = LocalContentColor.current,
            disabledBorderColor = MaterialTheme.colorScheme.outline,
            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        enabled = false
    )

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Выберите время") },
            text = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    NumberPickerColumn(
                        label = "Часы",
                        value = selectedTime.hour,
                        range = 0..23,
                        onValueChange = { selectedTime = selectedTime.withHour(it) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    NumberPickerColumn(
                        label = "Минуты",
                        value = selectedTime.minute,
                        range = 0..59,
                        onValueChange = { selectedTime = selectedTime.withMinute(it) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    NumberPickerColumn(
                        label = "Секунды",
                        value = selectedTime.second,
                        range = 0..59,
                        onValueChange = { selectedTime = selectedTime.withSecond(it) }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    onTimeSelected(selectedTime.format(formatter))
                    showDialog = false
                }) {
                    Text("ОК")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }
}

@Composable
fun NumberPickerColumn(
    label: String,
    value: Int,
    range: IntRange,
    onValueChange: (Int) -> Unit
) {
    Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
        Text(label)
        AndroidView(
            factory = { context ->
                NumberPicker(context).apply {
                    minValue = range.first
                    maxValue = range.last
                    this.value = value
                    setOnValueChangedListener { _, _, newVal -> onValueChange(newVal) }
                    wrapSelectorWheel = true
                }
            },
            update = {
                it.value = value
            }
        )
    }
}