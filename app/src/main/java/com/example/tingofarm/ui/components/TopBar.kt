package com.example.tingofarm.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.tingofarm.ui.settings.SettingsDialog
import com.example.tingofarm.ui.theme.TingoFarmTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    title: String,
    navController: NavController,
    onLogout: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    TopAppBar(
        title = { Text(text = title) },
        actions = {
            IconButton(onClick = { showDialog = true }) {
                Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings")
            }

            if (showDialog) {
                SettingsDialog(
                    navController = navController,
                    onDismiss = { showDialog = false },
                    onLogoutClick = {
                        onLogout()
                        showDialog = false
                    }
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.White,
            titleContentColor = Color.DarkGray
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun TopBarPreview() {
    TingoFarmTheme {
        val navController = rememberNavController()
        TopBar(
            title = "Tingo Farm Management",
            navController = navController,
            onLogout = { }
        )
    }
}