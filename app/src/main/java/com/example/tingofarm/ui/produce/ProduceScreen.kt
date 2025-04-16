package com.example.tingofarm.ui.produce

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.tingofarm.data.model.Produce
import com.example.tingofarm.data.repository.EmployeeRepository
import com.example.tingofarm.data.repository.LivestockRepository
import com.example.tingofarm.data.repository.ProduceRepository
import com.example.tingofarm.ui.components.BaseScreen
import com.example.tingofarm.viewmodel.AuthenticationViewModel
import com.example.tingofarm.viewmodel.EmployeeViewModel
import com.example.tingofarm.viewmodel.LivestockViewModel
import com.example.tingofarm.viewmodel.ProduceViewModel
import com.example.tingofarm.viewmodelfactory.EmployeeViewModelFactory
import com.example.tingofarm.viewmodelfactory.LivestockViewModelFactory
import com.example.tingofarm.viewmodelfactory.ProduceViewModelFactory
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProduceScreen(
    navController: NavHostController,
    authenticationViewModel: AuthenticationViewModel,
    employeeViewModel: EmployeeViewModel = viewModel(
        factory = EmployeeViewModelFactory(EmployeeRepository(FirebaseFirestore.getInstance()))
    ),
    livestockViewModel: LivestockViewModel = viewModel(
        factory = LivestockViewModelFactory(LivestockRepository(FirebaseFirestore.getInstance()))
    ),
    produceViewModel: ProduceViewModel = viewModel(
        factory = ProduceViewModelFactory(ProduceRepository(FirebaseFirestore.getInstance()))
    ),
) {
    var showDialog by remember { mutableStateOf(false) }
    val selectedDate = remember { mutableStateOf(System.currentTimeMillis()) }

    var selectedProduce by remember { mutableStateOf<Produce?>(null) }

    val employees = employeeViewModel.employees.collectAsState(initial = emptyList()).value
    val cows = livestockViewModel.livestock.collectAsState(initial = emptyList()).value
    val produceList = produceViewModel.produceList.collectAsState(initial = emptyList()).value

    if (cows.isEmpty() || employees.isEmpty()) {
        Text("No cows or employees available", color = Color.Red)
        return
    }

    BaseScreen(
        navController = navController,
        onLogout = {
            authenticationViewModel.logout()
            navController.navigate("login") {
                popUpTo(navController.graph.startDestinationId) { inclusive = true}
            }
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = "Daily milk produce",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    DatePickerDocked(
                        selectedDate = selectedDate
                    ) { dateMillis ->
                        selectedDate.value = dateMillis
                        produceViewModel.setDate(dateMillis)
                    }
                }

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { showDialog = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White
                    )
                ) {
                    Text("Add Today's Produce")
                }

                // Produce List as Cards
                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(produceList) { produce ->
                        ProduceCard(
                            produce = produce,
                            onEdit = { selectedProduce = it; showDialog = true },
                            onDelete = { produceViewModel.deleteProduce(it) }
                        )
                    }
                }

                if (showDialog) {
                    ProduceDialog(
                        onDismiss = { showDialog = false },
                        onConfirm = { cow, employee, morningQty, eveningQty ->
                            if (selectedProduce == null) {
                                produceViewModel.addProduce(
                                    produce = Produce(
                                        cow = cow,
                                        employee = employee,
                                        morningQty = morningQty.toDoubleOrNull() ?: 0.0,
                                        eveningQty = eveningQty.toDoubleOrNull() ?: 0.0,
                                        date = produceViewModel.getCurrentDate()
                                    )
                                )
                            } else {
                                selectedProduce?.let {
                                    produceViewModel.updateProduce(
                                        it.copy(
                                            cow = cow,
                                            employee = employee,
                                            morningQty = morningQty.toDoubleOrNull() ?: 0.0,
                                            eveningQty = eveningQty.toDoubleOrNull() ?: 0.0
                                        )
                                    )
                                }
                            }
                            selectedProduce = null
                            showDialog = false
                        },
                        cows = cows,
                        employees = employees,
                        initialData = selectedProduce
                    )
                }
            }
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDocked(
    selectedDate: MutableState<Long>,
    onDateSelected: (Long) -> Unit,
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDate.value)
    val formattedDate = convertMillisToDate(selectedDate.value)

    LaunchedEffect(datePickerState.selectedDateMillis) {
        datePickerState.selectedDateMillis?.let { newDateMillis ->
            if (newDateMillis != selectedDate.value) {
                selectedDate.value = newDateMillis
                onDateSelected(newDateMillis)
                showDatePicker = false
            }
        }
    }

    Row(
        modifier = Modifier
            .clickable { showDatePicker = true }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.DateRange,
            contentDescription = "Select date",
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = formattedDate,
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(start = 4.dp)
        )
    }


    if (showDatePicker) {
        Popup(
            onDismissRequest = { showDatePicker = false },
            alignment = Alignment.TopEnd
        ) {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                DatePicker(
                    state = datePickerState,
                    showModeToggle = false,
                    headline = { Text(formattedDate) }
                )

            }
        }
    }
}

fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    return formatter.format(Date(millis))
}

