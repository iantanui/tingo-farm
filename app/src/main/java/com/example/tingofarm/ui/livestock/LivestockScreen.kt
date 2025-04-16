package com.example.tingofarm.ui.livestock

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.tingofarm.data.model.Livestock
import com.example.tingofarm.ui.theme.TingoFarmTheme
import com.example.tingofarm.viewmodel.LivestockViewModel

@Composable
fun LivestockScreen(
    navController: NavController,
    livestockViewModel: LivestockViewModel = viewModel(),
) {
    var showDialog by remember { mutableStateOf(false) }
    var selectedLivestock by remember { mutableStateOf<Livestock?>(null) }

    // Collect livestock list from ViewModel
    val livestockList by livestockViewModel.livestock.collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Text(text = "Farm Livestock", fontSize = 16.sp)
        Text(text = "List of livestock", fontSize = 12.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { showDialog = true },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black,
                contentColor = Color.White
            )
        ) {
            Text("Add Livestock")
        }

        // Show LivestockDialog to add new livestock
        if (showDialog) {
            LivestockDialog(
                onDismiss = { showDialog = false },
                onConfirm = { name, breed, healthStatus ->

                    if (selectedLivestock == null) {
                        livestockViewModel.addLivestock(
                            livestock = Livestock(
                                name = name,
                                breed = breed,
                                healthStatus = healthStatus
                            )
                        )
                    } else {
                        selectedLivestock?.let {
                            livestockViewModel.updateLivestock(
                                it.id,
                                it.copy(
                                    name = name,
                                    breed = breed,
                                    healthStatus = healthStatus
                                )
                            )
                        }
                    }
                    showDialog = false
                    selectedLivestock = null
                },
                initialData = selectedLivestock
            )
        }

        // Table Header with borders
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .border(1.dp, Color.LightGray)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Text(
                    text = "Name",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(2f)
                )
                Text(
                    text = "Breed",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(2f)
                )
                Text(
                    text = "Health",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(2f)
                )
            }

            // Display the table rows
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(livestockList) { livestock ->
                    LivestockRow(
                        livestock = livestock,
                        onRowClick = { selectedLivestock = it }
                    )
                }
            }
        }


        selectedLivestock?.let { livestock ->
            LivestockCard(
                name = livestock.name,
                breed = livestock.breed,
                healthStatus = livestock.healthStatus,
                onEdit = {
                    selectedLivestock = livestock
                    showDialog = true
                },
                onDelete = {
                    livestockViewModel.deleteLivestock(livestock.id)
                }
            )
        }
    }
}

@Composable
fun LivestockRow(livestock: Livestock, onRowClick: (Livestock) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.LightGray)
            .clickable(onClick = { onRowClick(livestock) })
            .padding(vertical = 8.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = livestock.name, modifier = Modifier.weight(2f))
        Text(text = livestock.breed, modifier = Modifier.weight(2f))
        Text(text = livestock.healthStatus, modifier = Modifier.weight(2f))
    }
}


@Preview(showBackground = true)
@Composable
fun LivestockScreenPreview() {
    TingoFarmTheme {
        val navController = rememberNavController()
        LivestockScreen(navController = navController)
    }
}
