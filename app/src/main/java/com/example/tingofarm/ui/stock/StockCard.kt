package com.example.tingofarm.ui.stock

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.tingofarm.data.model.StockItem
import com.example.tingofarm.viewmodel.StockViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun StockCard(
    stockViewModel: StockViewModel,
    stockItem: StockItem,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onRefill: () -> Unit,
) {

    var expanded by remember { mutableStateOf(false) }
    var isCardExpanded by remember { mutableStateOf(false) }

    val daysSinceLastUpdate = stockViewModel.calculateDaysSinceLastUpdate(stockItem.date)
    val consumed = (daysSinceLastUpdate * stockItem.dailyConsumptionRate).toInt()
    val currentQuantity = (stockItem.quantity - consumed).coerceAtLeast(0)
    val initialQuantity =
        stockItem.history.lastOrNull { it.type == "Added" || it.type == "Refilled" }?.amount
            ?: stockItem.quantity
    val progress = if (initialQuantity > 0) {
        (currentQuantity.toFloat() / initialQuantity).coerceIn(0f, 1f)
    } else 0f
    val isLowStock = stockItem.quantity < stockItem.lowStockThreshold

    // Depletion calculation
    val depletionDays = if (stockItem.dailyConsumptionRate > 0) {
        (stockItem.quantity / stockItem.dailyConsumptionRate).toInt()
    } else 0
    val depletionDate = if (depletionDays > 0) {
        val latestDate = stockItem.history.maxByOrNull { it.date }?.date ?: Date()
        val depletionMillis = latestDate.time + (depletionDays * 24 * 60 * 60 * 1000L)
        SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(Date(depletionMillis))
    } else "N/A"

    // Latest history
    val latestHistory = stockItem.history.lastOrNull()
    val lastUpdatedText = latestHistory?.let {
        "Last Updated: ${SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(it.date)}"
    } ?: "No updates"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { isCardExpanded = !isCardExpanded },
        border = BorderStroke(1.dp, Color.LightGray),
        shape = MaterialTheme.shapes.small,
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {

                Text(
                    text = stockItem.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Supplier: ${stockItem.supplier}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal
                )

                LinearProgressIndicator(
                    progress = { progress.coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    color = if (isLowStock) Color.Red else Color.Green,
                    trackColor = Color.LightGray
                )

                if (isCardExpanded) {
                    Text(
                        text = "Qty: $currentQuantity / Low: ${stockItem.lowStockThreshold}",
                        fontSize = 14.sp,
                        color = if (isLowStock) Color.Red else Color.Black
                    )
                    Text(
                        text = "Depletion: $depletionDate",
                        fontSize = 14.sp,
                        color = if (depletionDays in (1..3)) Color.Red else Color.Black
                    )
                    latestHistory?.let {
                        Text(
                            text = "Last ${it.type}: ${it.amount} on ${
                                SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(it.date)
                            }",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            Column(modifier = Modifier.size(24.dp)) {

                IconButton(onClick = { expanded = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier
                        .background(Color.White)
                ) {
                    DropdownMenuItem(
                        text = { Text("Edit") },
                        onClick = {
                            expanded = false
                            onEdit()
                        },
                        leadingIcon = { Icon(Icons.Default.Edit, contentDescription = "Edit Icon") }
                    )
                    DropdownMenuItem(
                        text = { Text("Refill") },
                        onClick = {
                            expanded = false
                            onRefill()
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Refresh,
                                contentDescription = "Refill Icon"
                            )
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete") },
                        onClick = {
                            expanded = false
                            onDelete()
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete Icon"
                            )
                        }
                    )

                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = lastUpdatedText,
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(4.dp)
            )
        }

    }
}
