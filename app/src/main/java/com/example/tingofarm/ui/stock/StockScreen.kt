package com.example.tingofarm.ui.stock

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.tingofarm.data.model.StockItem
import com.example.tingofarm.data.repository.StockRepository
import com.example.tingofarm.ui.components.BaseScreen
import com.example.tingofarm.viewmodel.AuthenticationViewModel
import com.example.tingofarm.viewmodel.StockViewModel
import com.example.tingofarm.viewmodelfactory.StockViewModelFactory
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun StockScreen(
    navController: NavController,
    authenticationViewModel: AuthenticationViewModel,
    stockViewModel: StockViewModel = viewModel(
        factory = StockViewModelFactory(StockRepository(FirebaseFirestore.getInstance()))
    ),
) {
    var showDialog by remember { mutableStateOf(false) }
    var showRefillDialog by remember { mutableStateOf(false) }
    var selectedStockItem by remember { mutableStateOf<StockItem?>(null) }
    var showLowStockOnly by remember { mutableStateOf(false) }

    val stockItemList by stockViewModel.stockItems.collectAsState(initial = emptyList())
    val filteredStockList = if (showLowStockOnly) {
        stockItemList.filter {
            val currentQuantity = (it.quantity - (stockViewModel.calculateDaysSinceLastUpdate(it.date) * it.dailyConsumptionRate).toInt()).coerceAtLeast(0)
            currentQuantity < it.lowStockThreshold
        }
    } else {
        stockItemList
    }

    BaseScreen(
        navController = navController,
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                // Header with Low Stock Filter
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Stock Management",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Track your farm inventory",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                    IconButton(onClick = { showLowStockOnly = !showLowStockOnly }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.List,
                            contentDescription = "Toggle low stock filter",
                            tint = if (showLowStockOnly) MaterialTheme.colorScheme.primary else Color.Gray
                        )
                    }
                }

                // Add Button
                Button(
                    onClick = { showDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White
                    )
                ) {
                    Text("Add Stock Item")
                }

                // Stock Items as Cards
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredStockList) { stockItem ->
                        StockCard(
                            stockViewModel = stockViewModel,
                            stockItem = stockItem,
                            onEdit = {
                                selectedStockItem = stockItem
                                showDialog = true
                            },
                            onRefill = {
                                selectedStockItem = stockItem
                                showRefillDialog = true
                            },
                            onDelete = {
                                stockViewModel.deleteStockItem(stockItem.id)
                            }
                        )
                    }
                }

                // Dialog for Adding/Editing Stock
                if (showDialog) {
                    StockDialog(
                        onDismiss = {
                            showDialog = false
                            selectedStockItem = null
                        },
                        onConfirm = { name, quantity, supplier, lowThreshold, consumptionRate ->
                            if (selectedStockItem == null) {
                                stockViewModel.addStockItem(
                                    stockItem = StockItem(
                                        name = name,
                                        quantity = quantity.toIntOrNull() ?: 0,
                                        supplier = supplier,
                                        lowStockThreshold = lowThreshold.toIntOrNull() ?: 0,
                                        dailyConsumptionRate = consumptionRate.toDoubleOrNull() ?: 0.0
                                    )
                                )
                            } else {
                                selectedStockItem?.let {
                                    stockViewModel.updateStockItem(
                                        it.id,
                                        it.copy(
                                            name = name,
                                            quantity = quantity.toIntOrNull() ?: 0,
                                            supplier = supplier,
                                            lowStockThreshold = lowThreshold.toIntOrNull() ?: 0,
                                            dailyConsumptionRate = consumptionRate.toDoubleOrNull() ?: 0.0
                                        )
                                    )
                                }
                            }
                            showDialog = false
                            selectedStockItem = null
                        },
                        initialData = selectedStockItem
                    )
                }

                // Dialog for Refilling Stock
                if (showRefillDialog) {
                    RefillDialog(
                        stockItem = selectedStockItem,
                        onDismiss = {
                            showRefillDialog = false
                            selectedStockItem = null
                        },
                        onConfirm = { refillAmount ->
                            selectedStockItem?.let {
                                stockViewModel.refillStockItem(it.id, refillAmount.toIntOrNull() ?: 0) // Fixed type mismatch
                            }
                            showRefillDialog = false
                            selectedStockItem = null
                        }
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