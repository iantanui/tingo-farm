package com.example.tingofarm.ui.stock

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tingofarm.data.model.StockItem

@Composable
fun RefillDialog(
    stockItem: StockItem?,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    var refillAmount by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Refill ${stockItem?.name ?: "Stock"}") },
        text = {
            Column {
                Text(
                    text = "Current Quantity: ${stockItem?.quantity ?: 0}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = refillAmount,
                    onValueChange = { if (it.all { char -> char.isDigit() }) refillAmount = it },
                    label = { Text("Refill Amount") },
                    placeholder = { Text("e.g., 10") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(refillAmount) },
                enabled = refillAmount.isNotBlank()
            ) {
                Text("Refill")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Cancel")
            }
        }
    )
}