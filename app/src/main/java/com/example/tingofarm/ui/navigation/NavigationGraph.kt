package com.example.tingofarm.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.tingofarm.ui.authentication.LoginScreen
import com.example.tingofarm.ui.dashboard.DashboardScreen
import com.example.tingofarm.ui.employees.EmployeeScreen
import com.example.tingofarm.ui.livestock.LivestockScreen
import com.example.tingofarm.ui.manage.ManageScreen
import com.example.tingofarm.ui.produce.ProduceScreen
import com.example.tingofarm.ui.reports.ReportsScreen
import com.example.tingofarm.ui.settings.SettingsScreen
import com.example.tingofarm.ui.stock.StockScreen
import com.example.tingofarm.viewmodel.AuthenticationViewModel
import com.example.tingofarm.viewmodel.EmployeeViewModel
import com.example.tingofarm.viewmodel.LivestockViewModel
import com.example.tingofarm.viewmodel.ProduceViewModel
import com.example.tingofarm.viewmodel.ReportViewModel
import com.example.tingofarm.viewmodel.StockViewModel

@Composable
fun NavigationGraph(
    navController: NavHostController,
    employeeViewModel: EmployeeViewModel,
    livestockViewModel: LivestockViewModel,
    produceViewModel: ProduceViewModel,
    stockViewModel: StockViewModel,
    reportViewModel: ReportViewModel,
    authenticationViewModel: AuthenticationViewModel
) {

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                authenticationViewModel = authenticationViewModel,
                onLoginClick = { navController.navigate(BottomNavItem.Dashboard.route)}
            )
        }

        composable(BottomNavItem.Dashboard.route) {
            DashboardScreen(
                navController,
                authenticationViewModel = authenticationViewModel,
                employeeViewModel,
                livestockViewModel,
                produceViewModel,
                stockViewModel
            )
        }
        composable(BottomNavItem.Produce.route) {
            ProduceScreen(
                navController,
                authenticationViewModel = authenticationViewModel,
                employeeViewModel,
                livestockViewModel,
                produceViewModel
            )
        }
        composable(BottomNavItem.Employees.route) {
            EmployeeScreen(
                navController,
                employeeViewModel
            )
        }
        composable(BottomNavItem.Livestock.route) {
            LivestockScreen(
                navController,
                livestockViewModel
            )
        }
        composable(BottomNavItem.Stock.route) {
            StockScreen(
                navController,
                authenticationViewModel = authenticationViewModel,
                stockViewModel
            )
        }
        composable("settings") { SettingsScreen(navController) }
        composable("manage") {
            ManageScreen(
                navController,
                authenticationViewModel = authenticationViewModel,
                employeeViewModel,
                livestockViewModel
            )
        }
        composable(BottomNavItem.Report.route) {
            ReportsScreen(
                navController,
                authenticationViewModel = authenticationViewModel,
                produceViewModel,
                stockViewModel,
                reportViewModel
            )
        }
    }
}

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Dashboard : BottomNavItem("dashboard", Icons.Default.Home, "Dashboard")
    object Produce : BottomNavItem("produce", Icons.Default.Create, "Produce")
    object Employees : BottomNavItem("employees", Icons.Default.Menu, "Employees")
    object Livestock : BottomNavItem("livestock", Icons.Default.DateRange, "Livestock")
    object Stock : BottomNavItem("stock", Icons.AutoMirrored.Filled.List, "Stock")
    object Report : BottomNavItem("report", Icons.AutoMirrored.Filled.ExitToApp, "Report")
}
