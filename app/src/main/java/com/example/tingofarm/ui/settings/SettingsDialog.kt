package com.example.tingofarm.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun SettingsDialog(
    onDismiss: () -> Unit,
    navController: NavController,
    onLogoutClick: () -> Unit,
) {

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp),
            contentAlignment = Alignment.TopEnd
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .width(160.dp)
                    .offset(x = 16.dp)
                    .background(Color.White, shape = RoundedCornerShape(8.dp)),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                // Manage
                Row(
                    modifier = Modifier
                        .clickable {
                            navController.navigate("manage")
                            onDismiss()
                        }
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Build,
                        contentDescription = "Manage",
                        tint = Color.Black,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = "Manage",
                        fontSize = 18.sp
                    )
                }

                // Settings
                Row(
                    modifier = Modifier
                        .clickable {
                            navController.navigate("settings")
                            onDismiss()
                        }
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = Color.Black,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = "Settings",
                        fontSize = 18.sp
                    )
                }

                // Log out
                Row(
                    modifier = Modifier
                        .clickable {
                            onLogoutClick()
                            onDismiss()
                        }
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Log out",
                        tint = Color.Black,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = "Log out",
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsDialogPreview() {
    val navController = rememberNavController()
    SettingsDialog(
        onDismiss = { },
        navController = navController,
        onLogoutClick = { }
    )
}