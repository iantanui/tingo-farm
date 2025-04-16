package com.example.tingofarm.data.model

import java.util.Date

data class StockItem(
    val id: String = "",
    val name: String = "",
    val quantity: Int = 0,
    val supplier: String ="",
    val lowStockThreshold: Int = 0,
    val dailyConsumptionRate: Double = 0.0,
    val date: Long? = null,
    val history: List<StockHistoryEntry> = emptyList(),

)

data class StockHistoryEntry(
    val date: Date = Date(),
    val type: String = "",
    val amount: Int = 0
)