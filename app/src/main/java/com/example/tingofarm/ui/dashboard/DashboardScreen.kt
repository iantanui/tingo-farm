package com.example.tingofarm.ui.dashboard

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.tingofarm.ui.components.BaseScreen
import com.example.tingofarm.viewmodel.AuthenticationViewModel
import com.example.tingofarm.viewmodel.EmployeeViewModel
import com.example.tingofarm.viewmodel.LivestockViewModel
import com.example.tingofarm.viewmodel.ProduceViewModel
import com.example.tingofarm.viewmodel.StockViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun DashboardScreen(
    navController: NavHostController,
    authenticationViewModel: AuthenticationViewModel,
    employeeViewModel: EmployeeViewModel,
    livestockViewModel: LivestockViewModel,
    produceViewModel: ProduceViewModel,
    stockViewModel: StockViewModel,
) {

    BaseScreen(
        navController = navController,
        content = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .background(Color.White)
            ) {
                item {
                    Text(
                        text = "Dashboard",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    DashboardCard(
                        icon = { Icon(Icons.Outlined.DateRange, contentDescription = null) },
                        label = "Daily Milk Produce",
                        value = produceViewModel.dailyTotalProduce.collectAsState().value.toString(),
                        caption = "Milk from today"
                    )
                }

                item {
                    DashboardCard(
                        icon = { Icon(Icons.Outlined.Person, contentDescription = null) },
                        label = "Total Employees",
                        value = employeeViewModel.totalEmployees.collectAsState().value.toString(),
                        caption = "All employees"
                    )
                }

                item {
                    DashboardCard(
                        icon = { Icon(Icons.AutoMirrored.Outlined.List, contentDescription = null) },
                        label = "Total Livestock",
                        value = livestockViewModel.totalLivestock.collectAsState().value.toString(),
                        caption = "All livestock"
                    )
                }

                item {
                    DashboardCard(
                        icon = { Icon(Icons.Outlined.Check, contentDescription = null) },
                        label = "Healthy Livestock",
                        value = livestockViewModel.healthyLivestock.collectAsState().value.toString(),
                        caption = "Healthy livestock"
                    )
                }

                item {
                    DashboardCard(
                        icon = { Icon(Icons.Outlined.Clear, contentDescription = null) },
                        label = "Unhealthy Livestock",
                        value = livestockViewModel.unhealthyLivestock.collectAsState().value.toString(),
                        caption = "Unhealthy livestock"
                    )
                }

            }
        },
        onLogout = {
            authenticationViewModel.logout()
            navController.navigate("login") {
                popUpTo(navController.graph.startDestinationId) { inclusive = true}
            }
        }
    )
}
