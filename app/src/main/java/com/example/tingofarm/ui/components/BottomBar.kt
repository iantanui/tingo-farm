package com.example.tingofarm.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.tingofarm.ui.navigation.BottomNavItem
import com.example.tingofarm.ui.theme.TingoFarmTheme


@Composable
fun BottomBar(navController: NavController) {

    val items = listOf(
        BottomNavItem.Dashboard,
        BottomNavItem.Produce,
        BottomNavItem.Stock,
        BottomNavItem.Report
    )

    NavigationBar(
        containerColor = Color.White,
        contentColor = Color.DarkGray
    ) {
        // Get current route
        val navBackStackEntry = navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry.value?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            restoreState = true
                            launchSingleTop = true
                        }
                    }
                }
            )
        }

    }
}

@Preview(showBackground = true)
@Composable
fun BottomBarPreview() {
    TingoFarmTheme {
        val navController = rememberNavController()
        BottomBar(navController = navController)
    }
}