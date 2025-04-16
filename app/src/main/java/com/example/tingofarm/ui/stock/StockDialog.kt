package com.example.tingofarm.ui.stock

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tingofarm.data.model.StockItem

@Composable
fun StockDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String, String) -> Unit,
    initialData: StockItem? = null,
) {
    var name by remember { mutableStateOf(initialData?.name ?: "") }
    var quantity by remember { mutableStateOf(initialData?.quantity?.toString() ?: "") }
    var supplier by remember { mutableStateOf(initialData?.supplier ?: "") }
    var lowThreshold by remember { mutableStateOf(initialData?.lowStockThreshold?.toString() ?: "") }
    var consumptionRate by remember { mutableStateOf(initialData?.dailyConsumptionRate?.toString() ?: "") }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        containerColor = Color.White,
        title = { Text(text = if (initialData == null) "Add Stock Item" else "Edit Stock Item") },
        text = {
            Column {

                Text(text = "Name", fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 4.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = { Text("Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.LightGray, shape = RoundedCornerShape(8.dp)),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(text = "Quantity", fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 4.dp))
                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    placeholder = { Text("Quantity") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.LightGray, shape = RoundedCornerShape(8.dp)),
                    shape = RoundedCornerShape(8.dp),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(text = "Supplier", fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 4.dp))
                OutlinedTextField(
                    value = supplier,
                    onValueChange = { supplier = it },
                    placeholder = { Text("Supplier") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.LightGray, shape = RoundedCornerShape(8.dp)),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(text = "Low threshold", fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 4.dp))
                OutlinedTextField(
                    value = lowThreshold,
                    onValueChange = { lowThreshold = it },
                    placeholder = { Text("Low Stock Threshold") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.LightGray, shape = RoundedCornerShape(8.dp)),
                    shape = RoundedCornerShape(8.dp),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(text = "Daily rate", fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 4.dp))
                OutlinedTextField(
                    value = consumptionRate,
                    onValueChange = { if (it.isEmpty() || it.toDoubleOrNull() != null) consumptionRate = it },
                    placeholder = { Text("5.0 (units/day)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.LightGray, shape = RoundedCornerShape(8.dp)),
                    shape = RoundedCornerShape(8.dp),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal)
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(name, quantity, supplier, lowThreshold, consumptionRate )
                    name = ""
                    quantity = ""
                    supplier = ""
                    lowThreshold = ""
                    consumptionRate = ""

                },
                enabled = name.isNotBlank() && quantity.isNotBlank() && supplier.isNotBlank() && lowThreshold.isNotBlank()
            ) {
                Text(if (initialData == null) "Add" else "Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun StockDialogPreview() {
    StockDialog(
        onDismiss = { /* TODO */ },
        onConfirm = { _, _, _, _, _-> }
    )
}
