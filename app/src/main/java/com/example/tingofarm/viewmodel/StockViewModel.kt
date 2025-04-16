package com.example.tingofarm.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tingofarm.data.model.StockHistoryEntry
import com.example.tingofarm.data.model.StockItem
import com.example.tingofarm.data.repository.StockRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import java.util.concurrent.TimeUnit

class StockViewModel(private val repository: StockRepository) : ViewModel() {

    private val _stockItems = MutableStateFlow<List<StockItem>>(emptyList())
    val stockItems: StateFlow<List<StockItem>> = _stockItems.asStateFlow()

    init {
        fetchStockItems()
    }

    private fun fetchStockItems() {
        viewModelScope.launch {
            val items = repository.getStockItems()
            val updatedItems = items.map { updateStockQuantity(it) }
            _stockItems.value = updatedItems
            checkLowStockAlerts(updatedItems)
        }
    }

    fun addStockItem(stockItem: StockItem) {
        viewModelScope.launch {
            val newItem = stockItem.copy(
                history = listOf(
                    StockHistoryEntry(
                        date = Date(),
                        type = "Added",
                        amount = stockItem.quantity
                    )
                ),
                date = Date().time
            )
            repository.addStockItem(newItem)
            fetchStockItems() // Refresh the list after adding
        }
    }

    fun updateStockItem(id: String, updatedStockItem: StockItem) {
        viewModelScope.launch {
            val itemWithDate = updatedStockItem.copy(date = Date().time)
            repository.updateStockItem(id, itemWithDate)
            fetchStockItems() // Refresh the list after updating
        }
    }

    fun refillStockItem(id: String, amount: Int) {
        viewModelScope.launch {
            val currentItem = _stockItems.value.find { it.id == id } ?: return@launch
            val daysSinceLastUpdate = calculateDaysSinceLastUpdate(currentItem.date)
            val consumed = (daysSinceLastUpdate * currentItem.dailyConsumptionRate).toInt()
            val newQuantity = (currentItem.quantity - consumed + amount).coerceAtLeast(0)

            val newHistory = currentItem.history + StockHistoryEntry(
                date = Date(),
                type = "Refilled",
                amount = amount
            )
            val updatedItem = currentItem.copy(
                quantity = newQuantity,
                history = newHistory,
                date = Date().time
            )
            repository.updateStockItem(id, updatedItem)
            fetchStockItems() // Refresh the list after refilling
        }
    }

    fun deleteStockItem(id: String) {
        viewModelScope.launch {
            repository.deleteStockItem(id)
            fetchStockItems() // Refresh the list after deleting
        }
    }

    private fun updateStockQuantity(item: StockItem): StockItem {
        val daysSinceLastUpdate = calculateDaysSinceLastUpdate(item.date)
        val consumed = (daysSinceLastUpdate * item.dailyConsumptionRate).toInt()
        val currentQuantity = (item.quantity - consumed).coerceAtLeast(0)

        // Use the last history entry's amount as the initial quantity
        val initialQuantity = item.history.lastOrNull { it.type == "Added" || it.type == "Refilled" }?.amount ?: item.quantity

        return item.copy(quantity = currentQuantity)
    }

    fun getStockProgress(item: StockItem): Float {
        val daysSinceLastUpdate = calculateDaysSinceLastUpdate(item.date)
        val consumed = (daysSinceLastUpdate * item.dailyConsumptionRate).toInt()
        val currentQuantity = (item.quantity - consumed).coerceAtLeast(0)
        val initialQuantity = item.history.lastOrNull { it.type == "Added" || it.type == "Refilled" }?.amount ?: item.quantity
        return if (initialQuantity > 0) {
            (currentQuantity.toFloat() / initialQuantity).coerceIn(0f, 1f)
        } else 0f
    }

    fun calculateDaysSinceLastUpdate(lastUpdatedMillis: Long?): Long {
        if (lastUpdatedMillis == null) return 0L
        val now = Date().time
        val diffInMillis = now - lastUpdatedMillis
        return TimeUnit.MILLISECONDS.toDays(diffInMillis).coerceAtLeast(0L)
    }

    // Check for low stock and log alerts
    private fun checkLowStockAlerts(items: List<StockItem>) {
        items.forEach { item ->
            if (item.quantity < item.lowStockThreshold && item.lowStockThreshold > 0) {
                sendLowStockAlert(item)
            }
        }
    }

    // Send a low stock alert (placeholder)
    private fun sendLowStockAlert(item: StockItem) {
        println("Low stock alert: ${item.name} has ${item.quantity} left (threshold: ${item.lowStockThreshold})")
        // Could extend to UI toast or notification
    }
}