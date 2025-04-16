package com.example.tingofarm.ui.manage

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.tingofarm.ui.components.BaseScreen
import com.example.tingofarm.ui.employees.EmployeeScreen
import com.example.tingofarm.ui.livestock.LivestockScreen
import com.example.tingofarm.viewmodel.AuthenticationViewModel
import com.example.tingofarm.viewmodel.EmployeeViewModel
import com.example.tingofarm.viewmodel.LivestockViewModel

@Composable
fun ManageScreen(
    navController: NavController,
    authenticationViewModel: AuthenticationViewModel,
    employeeViewModel: EmployeeViewModel,
    livestockViewModel: LivestockViewModel,
) {

    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabTitles = listOf("Employees", "Livestock")

    BaseScreen(
        navController = navController,
        content = {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = Color.Black,
                    contentColor = Color.White
                ) {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            text = {
                                Text(
                                    title,
                                    color = if (selectedTabIndex == index) Color.LightGray else Color.White
                                )
                            },
                            selected = selectedTabIndex == index,
                            onClick = {
                                selectedTabIndex = index
                            }
                        )
                    }

                }

                Spacer(modifier = Modifier.height(16.dp))

                when (selectedTabIndex) {
                    0 -> EmployeeScreen(navController, employeeViewModel)
                    1 -> LivestockScreen(navController, livestockViewModel)
                }
            }
        },
        onLogout = {
            authenticationViewModel.logout()
            navController.navigate("login") {
                popUpTo(navController.graph.startDestinationId) { inclusive = true}
            }
        },
        )
}

@Preview(showBackground = true)
@Composable
fun ManageScreenPreview() {
    val navController = rememberNavController()
    val authenticationViewModel: AuthenticationViewModel = viewModel()
    val employeeViewModel: EmployeeViewModel = viewModel()
    val livestockViewModel: LivestockViewModel = viewModel()

    ManageScreen(
        navController = navController,
        authenticationViewModel = authenticationViewModel,
        employeeViewModel = employeeViewModel,
        livestockViewModel = livestockViewModel
    )
}

