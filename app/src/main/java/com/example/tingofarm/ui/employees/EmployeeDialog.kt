package com.example.tingofarm.ui.employees

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tingofarm.data.model.Employee

@Composable
fun EmployeeDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String) -> Unit,
    initialData: Employee? = null,
) {
    var name by remember { mutableStateOf(initialData?.name ?: "") }
    var phoneNumber by remember { mutableStateOf(initialData?.phoneNumber ?: "") }
    var idNo by remember { mutableStateOf(initialData?.idNo ?: "") }
    var residence by remember { mutableStateOf(initialData?.residence ?: "") }


    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(name, phoneNumber, idNo, residence)

                    if (initialData == null) {
                        name = ""
                        phoneNumber = ""
                        idNo = ""
                        residence = ""
                    }
                }) {
                Text(if (initialData == null) "Add" else "Update")
            }
        }, dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = { Text(text = if (initialData == null) "Add Employee" else "Edit Employee") },
        text = {
            Column {
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

                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    placeholder = { Text("Phone Number") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.LightGray, shape = RoundedCornerShape(8.dp)),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = idNo,
                    onValueChange = { idNo = it },
                    placeholder = { Text("ID Number") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.LightGray, shape = RoundedCornerShape(8.dp)),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = residence,
                    onValueChange = { residence = it },
                    placeholder = { Text("Residence") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.LightGray, shape = RoundedCornerShape(8.dp)),
                    shape = RoundedCornerShape(8.dp)
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun EmployeeDialogPreview() {
    EmployeeDialog(
        onDismiss = { /*TODO*/ },
        onConfirm = { _, _, _, _ -> }
    )
}