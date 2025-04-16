package com.example.tingofarm.ui.employees

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.tingofarm.data.model.Employee
import com.example.tingofarm.ui.theme.TingoFarmTheme
import com.example.tingofarm.viewmodel.EmployeeViewModel

@Composable
fun EmployeeScreen(
    navController: NavController,
    employeeViewModel: EmployeeViewModel = viewModel(),
) {
    var showDialog by remember { mutableStateOf(false) }
    val employeeList by employeeViewModel.employees.collectAsState()

    var selectedEmployee by remember { mutableStateOf<Employee?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Text(text = "Farm Employees", fontSize = 16.sp)
        Text(text = "List of farm employees", fontSize = 12.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { showDialog = true },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black,
                contentColor = Color.White
            )
        ) {
            Text("Add Employee")
        }

        // Show dialog when button clicked
        if (showDialog) {
            EmployeeDialog(
                onDismiss = { showDialog = false },
                onConfirm = { name, phoneNumber, idNo, residence ->
                    if (selectedEmployee == null) {
                        employeeViewModel.addEmployee(
                            Employee(
                                name = name,
                                phoneNumber = phoneNumber,
                                idNo = idNo,
                                residence = residence
                            )
                        )
                    } else {
                        selectedEmployee?.let {
                            employeeViewModel.updateEmployee(
                                it.id,
                                it.copy(
                                    name = name,
                                    phoneNumber = phoneNumber,
                                    idNo = idNo,
                                    residence = residence
                                )
                            )
                        }
                    }
                    showDialog = false
                },
                initialData = selectedEmployee
            )
        }

        // Display EmployeeCard
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(employeeList) { employee ->
                EmployeeCard(
                    name = employee.name,
                    phoneNumber = employee.phoneNumber,
                    idNo = employee.idNo,
                    residence = employee.residence,
                    onEdit = {
                        selectedEmployee = employee
                        showDialog = true
                    },
                    onDelete = {
                        employeeViewModel.deleteEmployee(employee.id)
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EmployeeScreenPreview() {
    TingoFarmTheme {
        val navController = rememberNavController()
        EmployeeScreen(navController = navController)
    }
}
