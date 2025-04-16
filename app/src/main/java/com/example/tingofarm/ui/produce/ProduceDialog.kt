package com.example.tingofarm.ui.produce

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tingofarm.data.model.Employee
import com.example.tingofarm.data.model.Livestock
import com.example.tingofarm.data.model.Produce

@Composable
fun ProduceDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String) -> Unit,
    cows: List<Livestock>,
    employees: List<Employee>,
    initialData: Produce? = null,
) {

    var selectedCow by remember {
        mutableStateOf(
            initialData?.cow ?: cows.firstOrNull()?.name ?: ""
        )
    }
    var selectedEmployee by remember {
        mutableStateOf(
            initialData?.employee ?: employees.firstOrNull()?.name ?: ""
        )
    }
    var morningQty by remember { mutableStateOf(initialData?.morningQty?.toString() ?: "") }
    var eveningQty by remember { mutableStateOf(initialData?.eveningQty?.toString() ?: "") }

    var cowDropdownExpanded by remember { mutableStateOf(false) }
    var employeeDropdownExpanded by remember { mutableStateOf(false) }

    var selectedTime by remember { mutableStateOf("Morning") }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(selectedCow, selectedEmployee, morningQty, eveningQty)
                    // Reset selections
                },
                enabled = selectedCow.isNotEmpty() && selectedEmployee.isNotEmpty() && morningQty.isNotEmpty() && eveningQty.isNotEmpty()
            ) {
                Text(if (initialData == null) "Add" else "Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = { Text("Add milk produce") },
        text = {
            Column {
                // Cow Dropdown
                Text(
                    text = "Cow",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.LightGray, shape = RoundedCornerShape(8.dp))
                        .clickable { cowDropdownExpanded = !cowDropdownExpanded }
                        .padding(16.dp)
                ) {
                    Text(text = selectedCow.ifEmpty { "Select cow" })
                }

                DropdownMenu(
                    expanded = cowDropdownExpanded,
                    onDismissRequest = { cowDropdownExpanded = false }
                ) {
                    cows.forEach { cowOption ->
                        DropdownMenuItem(
                            text = { Text(cowOption.name) }, // Use cow name for display
                            onClick = {
                                selectedCow = cowOption.name
                                cowDropdownExpanded = false
                                Log.d("ProduceDialog", "Selected Cow: ${cowOption.name}")
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Employee dropdown
                Text(
                    text = "Employee",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.LightGray, shape = RoundedCornerShape(8.dp))
                        .clickable { employeeDropdownExpanded = !employeeDropdownExpanded }
                        .padding(16.dp)
                ) {
                    Text(text = selectedEmployee.ifEmpty { "Select employee" })
                }

                DropdownMenu(
                    expanded = employeeDropdownExpanded,
                    onDismissRequest = { employeeDropdownExpanded = false }
                ) {
                    employees.forEach { employeeOption ->
                        DropdownMenuItem(
                            text = { Text(employeeOption.name) }, // Use employee name for display
                            onClick = {
                                selectedEmployee = employeeOption.name
                                employeeDropdownExpanded = false
                                Log.d("ProductDialog", "Selected Employee: ${employeeOption.name}")
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                // Radio button for quantity selection
                Text(
                    text = "Time of day",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Row(
                        modifier = Modifier
                            .selectable(
                                selected = (selectedTime == "Morning"),
                                onClick = { selectedTime = "Morning" }
                            )
                            .padding(end = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (selectedTime == "Morning"),
                            onClick = null
                        )
                        Text(text = "Morning")
                    }

                    Row(
                        modifier = Modifier
                            .selectable(
                                selected = (selectedTime == "Evening"),
                                onClick = { selectedTime = "Evening" }
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (selectedTime == "Evening"),
                            onClick = null
                        )
                        Text(text = "Evening")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Quantity
                OutlinedTextField(
                    value = if (selectedTime == "Morning") morningQty else eveningQty,
                    onValueChange = {
                        if (selectedTime == "Morning") morningQty = it else eveningQty = it
                    },
                    label = { Text("${selectedTime} Quantity") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun ProduceDialogPreview() {
    ProduceDialog(
        onDismiss = {},
        onConfirm = { _, _, _, _ -> },
        cows = emptyList(),
        employees = emptyList()
    )
}