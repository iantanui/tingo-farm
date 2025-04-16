package com.example.tingofarm.ui.livestock

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tingofarm.data.model.Livestock

@Composable
fun LivestockDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String) -> Unit,
    initialData: Livestock? = null,
) {
    var livestockName by remember { mutableStateOf(initialData?.name ?: "") }
    var breed by remember { mutableStateOf(initialData?.breed ?: "") }
    var healthStatus by remember { mutableStateOf(initialData?.healthStatus ?: "") }

    var breedExpanded by remember { mutableStateOf(false) }
    var healthStatusExpanded by remember { mutableStateOf(false) }

    val breeds = listOf("Jersey", "Freshian", "Ayrshire", "Guernsey")
    val healthStatuses = listOf("Healthy", "Under Treatment")

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Add Livestock") },
        text = {
            Column {

                OutlinedTextField(
                    value = livestockName,
                    onValueChange = { livestockName = it },
                    label = null,
                    placeholder = { Text("Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.LightGray, shape = RoundedCornerShape(8.dp)),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Box(modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.LightGray, shape = MaterialTheme.shapes.small)
                    .clickable { breedExpanded = !breedExpanded }
                    .padding(16.dp)
                ) {
                    Text(text = breed.ifEmpty { "Select breed" })
                }

                DropdownMenu(
                    expanded = breedExpanded,
                    onDismissRequest = { breedExpanded = false }
                ) {
                    breeds.forEach { breedOption ->
                        DropdownMenuItem(
                            text = { Text(breedOption) },
                            onClick = {
                                breed = breedOption
                                breedExpanded = false
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Box(modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.LightGray, shape = MaterialTheme.shapes.small)
                    .clickable { healthStatusExpanded = !healthStatusExpanded }
                    .padding(16.dp)
                ) {
                    Text(text = healthStatus.ifEmpty { "Select health status" })
                }
                DropdownMenu(
                    expanded = healthStatusExpanded,
                    onDismissRequest = { healthStatusExpanded = false }
                ) {
                    healthStatuses.forEach { statusOption ->
                        DropdownMenuItem(
                            text = { Text(statusOption) },
                            onClick = {
                                healthStatus = statusOption
                                healthStatusExpanded = false
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(livestockName, breed, healthStatus)
                    livestockName = ""
                    breed = ""
                    healthStatus = ""
                },
                enabled = breed.isNotEmpty() && livestockName . isNotEmpty ()
            ) {
                Text("Add")
            }
        },

        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}

@Preview(showBackground = true)
@Composable
fun LivestockDialogPreview() {
    LivestockDialog(
        onDismiss = { /*TODO*/ },
        onConfirm = { _, _, _ -> })
}